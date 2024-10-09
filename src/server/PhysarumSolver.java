package server;
import client.Client;
import client.ClientController;
import item.Beacon;
import item.BeaconCluster;
import item.Link;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PhysarumSolver {

    private static final double INF = 10000.0;
    private static final double NEG = -1.0;
    private static final double GAMMA = 1.5;
    private static final double DELTA_TIME = 0.01;
    private static final int PLOT = 1;
    private static final double INIT_THICKNESS = 100.0;
    private static final double INIT_LENGTH = 1.0;
    private static final double THRESHOLD_1 = 0.5;
    private static final double THRESHOLD_2 = 1.0;

    // 基本パラメータ
    private final ArrayList<ArrayList<Link>> link = new ArrayList<>();
    private ArrayList<Double> Q_Kirchhoff;
    private ArrayList<Double> P_tubePressure;
    private ArrayList<ArrayList<Double>> Q_tubeFlow;
    private ArrayList<ArrayList<Double>> D_tubeThickness;
    private ArrayList<ArrayList<Double>> L_tubeLength;

    // 計算パラメータ
    private ArrayList<Double> Q_Kirchhoff_sinkExcept;
    private ArrayList<Double> P_tubePressure_sinkExcept;
    private ArrayList<ArrayList<Double>> D_tubeThickness_deltaT;
    private ArrayList<ArrayList<Double>> pressureCoefficient;
    private ArrayList<ArrayList<Double>> pressureCoefficient_sinkExcept;

    // シグモイド関数用
    private ArrayList<ArrayList<Double>> Q_tubeFlow_sigmoidOutput;

    // Pajekデータ
    private ArrayList<Double> x_coordinate;
    private ArrayList<Double> y_coordinate;
    private ArrayList<ArrayList<Double>> node_distance;

    private Client client;
    private ClientController clientController;


    public PhysarumSolver(int node) {
        initialize(node);
    }


    public void initialize(int node) {
        int nodeExcept = node - 1;

        // 1xN matrix
        Q_Kirchhoff = new ArrayList<>(node);
        P_tubePressure = new ArrayList<>(node);
        Q_Kirchhoff_sinkExcept = new ArrayList<>(nodeExcept);
        P_tubePressure_sinkExcept = new ArrayList<>(nodeExcept);

        // 初期値を追加してサイズを確保
        for (int i = 0; i < node; i++) {
            Q_Kirchhoff.add(0.0);  // 初期値を追加
            P_tubePressure.add(0.0);  // 初期値を追加
        }

        for (int i = 0; i < nodeExcept; i++) {
            Q_Kirchhoff_sinkExcept.add(0.0);  // 初期値を追加
            P_tubePressure_sinkExcept.add(0.0);  // 初期値を追加
        }

        // 2xN matrix
        Q_tubeFlow = createMatrix(node, node);
        D_tubeThickness = createMatrix(node, node);
        L_tubeLength = createMatrix(node, node);
        D_tubeThickness_deltaT = createMatrix(node, node);
        pressureCoefficient = createMatrix(node, node);
        pressureCoefficient_sinkExcept = createMatrix(nodeExcept, nodeExcept);
        Q_tubeFlow_sigmoidOutput = createMatrix(node, node);
        x_coordinate = new ArrayList<>(node);
        y_coordinate = new ArrayList<>(node);
        node_distance = createMatrix(node, node);

        // node数に応じてArrayList<Link>を初期化
        for (int i = 0; i < node; i++) {
            ArrayList<Link> innerList = new ArrayList<>(node); // 初期容量 node の ArrayList を作成
            for (int j = 0; j < node; j++) {
                innerList.add(new Link()); // Link オブジェクトを追加
            }
            link.add(innerList); // 外側の ArrayList に追加
        }


        // x_coordinate と y_coordinate の初期化
        for (int i = 0; i < node; i++) {
            x_coordinate.add(0.0);  // 初期値を追加
            y_coordinate.add(0.0);  // 初期値を追加
        }
    }

    //フィールドをすべてリセットする
    public void reset(){
        Q_Kirchhoff.clear();
        P_tubePressure.clear();
        Q_Kirchhoff_sinkExcept.clear();
        P_tubePressure_sinkExcept.clear();
        Q_tubeFlow.clear();
        D_tubeThickness.clear();
        L_tubeLength.clear();
        D_tubeThickness_deltaT.clear();
        pressureCoefficient.clear();
        pressureCoefficient_sinkExcept.clear();
        Q_tubeFlow_sigmoidOutput.clear();
        x_coordinate.clear();
        y_coordinate.clear();
        node_distance.clear();
        link.clear();
    }


    private ArrayList<ArrayList<Double>> createMatrix(int rows, int cols) {
        ArrayList<ArrayList<Double>> matrix = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            ArrayList<Double> row = new ArrayList<>(cols);
            for (int j = 0; j < cols; j++) {
                row.add(0.0);
            }
            matrix.add(row);
        }
        return matrix;
    }

    // nodeConfigureメソッドの追加
    public void linkConfigure(String NET_file, int node, BeaconCluster beaconList) throws IOException {
        double maxDistance = Math.sqrt(2);  // 最大距離 sqrt(2)

        // ファイル出力処理
        try (FileWriter writer = new FileWriter(new File(NET_file))) {
            writer.write("*Vertices\t" + node + "\n");
            for (int i = 0; i < node; i++) {
                if (i == beaconList.getBeacon(0).getId() || i == beaconList.getBeacon(1).getId()) {
                    writer.write(String.format("%d \"%d\" %.4f %.4f ic Black\n", i + 1, i + 1, beaconList.getBeacon(i).getX(), beaconList.getBeacon(i).getY()));
                } else {
                    writer.write(String.format("%d \"%d\" %.4f %.4f ic White\n", i + 1, i + 1, beaconList.getBeacon(i).getX(), beaconList.getBeacon(i).getY()));
                }
            }
            writer.write("*Arcs\n*Edges\n");


            for (int i = 0; i < node; i++) {
                for (int j = 0; j < node; j++) {
                    if (i == j) {
                        link.get(i).get(j).setD_tubeThickness(0.0);
                        link.get(i).get(j).setL_tubeLength(INF);
                        /**
                         D_tubeThickness.get(i).set(j, 0.0);
                         L_tubeLength.get(i).set(j, INF);
                         */
                    } else {
                        link.get(i).get(j).setDistance(Math.sqrt(Math.pow(beaconList.getBeacon(i).getX() - beaconList.getBeacon(j).getX(), 2) + Math.pow(beaconList.getBeacon(i).getY() - beaconList.getBeacon(j).getY(), 2)));
                        //node_distance.get(i).set(j, Math.sqrt(Math.pow(x_coordinate.get(j) - x_coordinate.get(i), 2) + Math.pow(y_coordinate.get(j) - y_coordinate.get(i), 2)));
                        double distance = link.get(i).get(j).getDistance();
                        //double distance = node_distance.get(i).get(j);
                        //デバッグ
                        System.out.println("node_distance[" + i + "][" + j + "] = " + distance);
                        if (0.0 < distance && distance <= THRESHOLD_1) {
                            writer.write(String.format("%d %d 1\n", i + 1, j + 1));
                            link.get(i).get(j).setLink(beaconList.getBeacon(i), beaconList.getBeacon(j), 10);
                            link.get(i).get(j).setD_tubeThickness(INIT_THICKNESS);
                            link.get(i).get(j).setL_tubeLength(INIT_LENGTH);
                            link.get(i).get(j).setCongestionRate(INIT_THICKNESS);
                            /**
                             D_tubeThickness.get(i).set(j, INIT_THICKNESS);
                             L_tubeLength.get(i).set(j, INIT_LENGTH);
                             */
                        } else if (THRESHOLD_1 < distance && distance <= THRESHOLD_2) {
                            writer.write(String.format("%d %d 1\n", i + 1, j + 1));
                            link.get(i).get(j).setLink(beaconList.getBeacon(i), beaconList.getBeacon(j), 10);
                            link.get(i).get(j).setD_tubeThickness(INIT_THICKNESS);
                            link.get(i).get(j).setL_tubeLength(INIT_LENGTH);
                            link.get(i).get(j).setCongestionRate(INIT_THICKNESS);
                            /**
                             D_tubeThickness.get(i).set(j, INIT_THICKNESS);
                             L_tubeLength.get(i).set(j, INIT_LENGTH);
                             */
                        } else if (THRESHOLD_2 < distance && distance <= maxDistance) {
                            writer.write(String.format("%d %d 1\n", i + 1, j + 1));
                            link.get(i).get(j).setLink(beaconList.getBeacon(i), beaconList.getBeacon(j), 10);
                            link.get(i).get(j).setD_tubeThickness(INIT_THICKNESS);
                            link.get(i).get(j).setL_tubeLength(INIT_LENGTH);
                            link.get(i).get(j).setCongestionRate(INIT_THICKNESS);
                            /**
                             D_tubeThickness.get(i).set(j, INIT_THICKNESS);
                             L_tubeLength.get(i).set(j, INIT_LENGTH);
                             */
                        } else {
                            link.get(i).get(j).setL_tubeLength(INF);
                        }
                    }
                }
            }
        }
    }

    public void routeRequest(Client client){

    }



    // setTopologyColorメソッドの追加
    public void setTopologyColor(int node, Beacon SOURCE, Beacon DIST, double eps, double Q_allFlow, int ct) throws IOException {
        String filename = "src/result/test_topology_" + (ct + 1) + ".net";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("*Vertices\t" + node + "\n");
            for (int i = 0; i < node; i++) {
                if (i == SOURCE.getId() || i == DIST.getId()) {
                    writer.write(String.format("%d \"%d\" %.4f %.4f ic Black\n", i + 1, i + 1, x_coordinate.get(i), y_coordinate.get(i)));
                } else {
                    writer.write(String.format("%d \"%d\" %.4f %.4f ic White\n", i + 1, i + 1, x_coordinate.get(i), y_coordinate.get(i)));
                }
            }
            writer.write("*Arcs\n*Edges\n");

            for (int i = 0; i < node; i++) {
                for (int j = 0; j < node; j++) {
                    if (link.get(i).get(j).getL_tubeLength() != INF){//L_tubeLength.get(i).get(j) != INF) {
                        double flow = link.get(i).get(j).getQ_tubeFlow();//Q_tubeFlow.get(i).get(j);
                        if (0 < link.get(i).get(j).getDistance() && link.get(i).get(j).getDistance() <= Math.sqrt(2)) {//if (0 < node_distance.get(i).get(j) && node_distance.get(i).get(j) <= Math.sqrt(2))
                            if (flow > 0 && flow <= eps) {
                                // Small flow, no color
                            } else if (flow > eps && flow <= THRESHOLD_1) {
                                writer.write(String.format("%d %d 1 c Blue\n", i + 1, j + 1));
                            } else if (flow > THRESHOLD_1 && flow <= THRESHOLD_2) {
                                writer.write(String.format("%d %d 2 c Green\n", i + 1, j + 1));
                            } else if (flow > THRESHOLD_2 && flow <= Q_allFlow) {
                                writer.write(String.format("%d %d 3 c Red\n", i + 1, j + 1));
                            }
                        }
                    }
                }
            }
        }
    }
    public void run(int node, int source, int dist, double Q_allFlow, int numLoop, String filePath) throws IOException {
        int nodeExcept = node - 1;
        int ct = 0;
        double eps = 1e-10;
        int[] testIter = {10};
        int a=0, b, i, j;
        double degeneracyEffect = 1.0;

        /**
        // ノード構成の設定（Pajekファイルから読み込む）
        linkConfigure(filePath, node, source, dist);
         */

        while (ct < numLoop) {
            // Kirchhoffの初期設定
            Q_Kirchhoff.set(source, Q_allFlow);
            Q_Kirchhoff.set(dist, Q_allFlow * NEG);
            // 圧力勾配の導出
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (L_tubeLength.get(i).get(j) != INF) { // ノードiとノードjが直接接続されている場合
                        if (i != j) { // iとjが異なる場合
                            pressureCoefficient.get(i).set(j, D_tubeThickness.get(i).get(j) / L_tubeLength.get(i).get(j) * NEG); // 圧力係数を計算
                        }
                    }
                }
            }

            int k = 0;
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (L_tubeLength.get(i).get(j) != INF) { // ノードiとノードjが直接接続されている場合
                        pressureCoefficient.get(k).set(k, pressureCoefficient.get(k).get(k) + D_tubeThickness.get(i).get(j) / L_tubeLength.get(i).get(j)); // 対角成分を加算
                    }
                }
                k++;
            }

            // シンクノードを除いた圧力係数行列の計算

            for (i = 0, a = 0; i < node && a < nodeExcept; i++, a++) {
                if (i == dist && dist != node) { // iがシンクノードである場合
                    i++; // シンクノードをスキップ
                }
                for (j = 0, b = 0; j < node && b < nodeExcept; j++, b++) {
                    if (j == dist) { // jがシンクノードの場合
                        j++; // シンクノードをスキップ
                    }
                    pressureCoefficient_sinkExcept.get(a).set(b, pressureCoefficient.get(i).get(j)); // シンクノードを除いた圧力係数行列を作成
                }
            }


            // sinkExcept 配列の準備
            for (a = 0, i = 0; i < node; i++) {
                if (i != dist) {
                    Q_Kirchhoff_sinkExcept.set(a++, Q_Kirchhoff.get(i));
                }
            }

            // ICCG法で圧力勾配を計算
            if (!ICCG.iccg(pressureCoefficient_sinkExcept, Q_Kirchhoff_sinkExcept, P_tubePressure_sinkExcept, nodeExcept, testIter, new double[]{eps})) {
                break;
            }

            // 圧力値の反映
            for (a = 0, i = 0; i < node; i++) {
                if (i == dist) {
                    P_tubePressure.set(i, 0.0);
                } else {
                    P_tubePressure.set(i, P_tubePressure_sinkExcept.get(a++));
                }
            }

            // 流量の計算
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (L_tubeLength.get(i).get(j) != INF) {
                        Q_tubeFlow.get(i).set(j, (D_tubeThickness.get(i).get(j) / L_tubeLength.get(i).get(j)) * (P_tubePressure.get(i) - P_tubePressure.get(j)));
                    }
                }
            }

            // シグモイド関数
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (L_tubeLength.get(i).get(j) != INF) {
                        Q_tubeFlow_sigmoidOutput.get(i).set(j, Math.pow(Math.abs(Q_tubeFlow.get(i).get(j)), GAMMA) / (1 + Math.pow(Math.abs(Q_tubeFlow.get(i).get(j)), GAMMA)));
                    }
                }
            }

            // チューブ厚の更新
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (L_tubeLength.get(i).get(j) != INF) {
                        double deltaThickness = (Q_tubeFlow_sigmoidOutput.get(i).get(j) - (degeneracyEffect * D_tubeThickness.get(i).get(j))) * DELTA_TIME;
                        D_tubeThickness_deltaT.get(i).set(j, deltaThickness);
                        D_tubeThickness.get(i).set(j, D_tubeThickness.get(i).get(j) + deltaThickness);
                    }
                }
            }

            // 結果のプロット
            if ((ct + 1) % PLOT == 0) {
                System.out.println("Iteration: " + (ct+1));
                /**
                setTopologyColor(node, source, dist, eps, Q_allFlow, ct);
                 */
            }

            ct++;
        }
    }
    /**
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // ノード数とファイル入力（仮）
        System.out.print("ノード数を入力してください: ");
        int node = scanner.nextInt();
        System.out.print("流量を入力してください: ");
        double Q_allFlow = scanner.nextDouble();
        System.out.print("反復回数を入力してください: ");
        int numLoop = scanner.nextInt();

        // ファイルパスの設定
        String filePath = "src/result/practice.net";

        PhysarumSolver solver = new PhysarumSolver(node);

        solver.run(node, 0, 1, Q_allFlow, numLoop, filePath);

        scanner.close();
    }
     */
}
