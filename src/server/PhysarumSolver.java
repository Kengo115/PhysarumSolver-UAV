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
import java.util.Arrays;
import java.util.List;


public class PhysarumSolver {

    private static final double INF = 10000.0;
    private static final double NEG = -1.0;
    private static final double GAMMA = 1.5;
    private static final double DELTA_TIME = 0.01;
    private static final int PLOT = 1;
    private static final double INIT_THICKNESS = 0.5;
    private static final double INIT_LENGTH = 1.0;
    private static final double INIT_RATE = 100.0;
    private static final double THRESHOLD_1 = 0.5;
    private static final double THRESHOLD_2 = 1.0;
    private static int node;
    private int runCounter = 0;
    private boolean fig_SOURCE = false;
    private boolean fig_DIST = false;
    private static final double coefficient_tanh = 1;
    // 基本パラメータ
    private Link[][] link;

    private double[] Q_Kirchhoff;
    private double[] P_tubePressure;

    // 計算パラメータ

    private double[][] D_tubeThickness_deltaT;
    private double[][] pressureCoefficient;

    // シグモイド関数用
    private double[][] Q_tubeFlow_sigmoidOutput;

    private Client client;
    private ClientController clientController;
    private BeaconCluster beaconCluster;
    ArrayList<Integer> sourceCluster = new ArrayList<>(5);
    ArrayList<Integer> distCluster = new ArrayList<>(5);
    private double[][] Flow_Capacity;
    private int[][] tubeFlow;
    private int[][] adjMatrix;
    private int UAV_counter = 0;
    private int num_pajek = 1;
    private int num_txt = 1;
    private int num_excel = 1;
    private int min_Flow = 100;
    int UAV_count;


    public PhysarumSolver(int node) {
        initialize(node);
    }


    public void initialize(int node) {
        int nodeExcept = node - 1;

        // 1xN matrix
        this.Q_Kirchhoff = new double[node];
        this.P_tubePressure = new double[node];
        // 初期値を追加してサイズを確保

        // 2xN matrix
        this.pressureCoefficient = new double[node][node];
        this.D_tubeThickness_deltaT = new double[node][node];
        this.Q_tubeFlow_sigmoidOutput = new double[node][node];
        this.Flow_Capacity = new double[node][node];
        this.tubeFlow = new int[node][node];
        this.adjMatrix = new int[node][node];


        // node数に応じてArrayList<Link>を初期化
        link = new Link[node][node]; // `node x node` の2次元配列を作成
        for (int i = 0; i < node; i++) {
            for (int j = 0; j < node; j++) {
                link[i][j] = new Link(); // 各要素に Link オブジェクトを追加
            }
        }
    }

    //フィールドをすべてリセットする
    public void reset(){
        Arrays.fill(Q_Kirchhoff, 0.0);
        Arrays.fill(P_tubePressure, 0.0);
        // 2次元配列の初期化
        for (int i = 0; i < pressureCoefficient.length; i++) {
            for (int j = 0; j < pressureCoefficient[i].length; j++) {
                pressureCoefficient[i][j] = 0.0;  // すべての要素に0.0を設定
                D_tubeThickness_deltaT[i][j] = 0.0;
                Q_tubeFlow_sigmoidOutput[i][j] = 0.0;
                Flow_Capacity[i][j] = 0.0;
                tubeFlow[i][j] = 0;
                adjMatrix[i][j] = 0;
            }
        }
    }


    // nodeConfigureメソッドの追加
    public void setLink(int node, BeaconCluster beaconList){
        this.node = node;
        this.beaconCluster = beaconList;
        /**
         //nodeDistanceによってリンクを決定
         double maxDistance = Math.sqrt(2);  // 最大距離 sqrt(2)

         for (int i = 0; i < node; i++) {
         for (int j = 0; j < node; j++) {
         if (i == j) {
         link.get(i).get(j).setD_tubeThickness(0.0);
         link.get(i).get(j).setL_tubeLength(INF);
         } else {
         link.get(i).get(j).setDistance(Math.sqrt(Math.pow(beaconList.getBeacon(i).getX() - beaconList.getBeacon(j).getX(), 2) + Math.pow(beaconList.getBeacon(i).getY() - beaconList.getBeacon(j).getY(), 2)));
         double distance = link.get(i).get(j).getDistance();

         System.out.println("node_distance[" + i + "][" + j + "] = " + distance);

         if(distance > maxDistance) {
         link.get(i).get(j).setL_tubeLength(INF);
         }else {
         link.get(i).get(j).setLink(beaconList.getBeacon(i), beaconList.getBeacon(j), 20);
         link.get(i).get(j).setD_tubeThickness(INIT_THICKNESS);
         link.get(i).get(j).setL_tubeLength(INIT_LENGTH);
         link.get(i).get(j).setCongestionRate(INIT_RATE);
         }
         }
         }
         }
         */

        //手動でリンクを決定
        for(int i=0; i<node; i++){
            for(int j=0; j<node; j++){
                link[i][j].setD_tubeThickness(0.0);
                link[i][j].setL_tubeLength(INF);
                //link.get(i).get(j).setDistance(Math.sqrt(Math.pow(beaconList.getBeacon(i).getX() - beaconList.getBeacon(j).getX(), 2) + Math.pow(beaconList.getBeacon(i).getY() - beaconList.getBeacon(j).getY(), 2)));
            }
        }

        link[0][1].setLink(beaconList.getBeacon(0), beaconList.getBeacon(1), 10);
        link[0][1].setD_tubeThickness(INIT_THICKNESS);
        link[0][1].setL_tubeLength(1);
        link[0][1].setDistance(1000);
        link[0][1].setCongestionRate(INIT_RATE);
        adjMatrix[0][1] = 1;

        link[1][0].setLink(beaconList.getBeacon(1), beaconList.getBeacon(0), 10);
        link[1][0].setD_tubeThickness(INIT_THICKNESS);
        link[1][0].setL_tubeLength(1);
        link[1][0].setDistance(1000);
        link[1][0].setCongestionRate(INIT_RATE);
        adjMatrix[1][0] = 1;

        link[0][2].setLink(beaconList.getBeacon(0), beaconList.getBeacon(4), 30);
        link[0][2].setD_tubeThickness(INIT_THICKNESS);
        link[0][2].setL_tubeLength(2);
        link[0][2].setDistance(2000);
        link[0][2].setCongestionRate(INIT_RATE);
        adjMatrix[0][2] = 1;

        link[2][0].setLink(beaconList.getBeacon(4), beaconList.getBeacon(0), 30);
        link[2][0].setD_tubeThickness(INIT_THICKNESS);
        link[2][0].setL_tubeLength(2);
        link[2][0].setDistance(2000);
        link[2][0].setCongestionRate(INIT_RATE);
        adjMatrix[2][0] = 1;

        link[0][3].setLink(beaconList.getBeacon(1), beaconList.getBeacon(2), 20);
        link[0][3].setD_tubeThickness(INIT_THICKNESS);
        link[0][3].setL_tubeLength(3);
        link[0][3].setDistance(3000);
        link[0][3].setCongestionRate(INIT_RATE);
        adjMatrix[0][3] = 1;

        link[3][0].setLink(beaconList.getBeacon(2), beaconList.getBeacon(1), 20);
        link[3][0].setD_tubeThickness(INIT_THICKNESS);
        link[3][0].setL_tubeLength(3);
        link[3][0].setDistance(3000);
        link[3][0].setCongestionRate(INIT_RATE);
        adjMatrix[3][0] = 1;

        link[1][4].setLink(beaconList.getBeacon(1), beaconList.getBeacon(3), 20);
        link[1][4].setD_tubeThickness(INIT_THICKNESS);
        link[1][4].setL_tubeLength(2);
        link[1][4].setDistance(2000);
        link[1][4].setCongestionRate(INIT_RATE);
        adjMatrix[1][4] = 1;

        link[4][1].setLink(beaconList.getBeacon(3), beaconList.getBeacon(1), 20);
        link[4][1].setD_tubeThickness(INIT_THICKNESS);
        link[4][1].setL_tubeLength(2);
        link[4][1].setDistance(2000);
        link[4][1].setCongestionRate(INIT_RATE);
        adjMatrix[4][1] = 1;

        link[2][3].setLink(beaconList.getBeacon(2), beaconList.getBeacon(3), 10);
        link[2][3].setD_tubeThickness(INIT_THICKNESS);
        link[2][3].setL_tubeLength(1);
        link[2][3].setDistance(1000);
        link[2][3].setCongestionRate(INIT_RATE);
        adjMatrix[2][3] = 1;

        link[3][2].setLink(beaconList.getBeacon(3), beaconList.getBeacon(2), 10);
        link[3][2].setD_tubeThickness(INIT_THICKNESS);
        link[3][2].setL_tubeLength(1);
        link[3][2].setDistance(1000);
        link[3][2].setCongestionRate(INIT_RATE);
        adjMatrix[3][2] = 1;

        link[2][5].setLink(beaconList.getBeacon(2), beaconList.getBeacon(5), 20);
        link[2][5].setD_tubeThickness(INIT_THICKNESS);
        link[2][5].setL_tubeLength(3);
        link[2][5].setDistance(3000);
        link[2][5].setCongestionRate(INIT_RATE);
        adjMatrix[2][5] = 1;

        link[5][2].setLink(beaconList.getBeacon(5), beaconList.getBeacon(2), 20);
        link[5][2].setD_tubeThickness(INIT_THICKNESS);
        link[5][2].setL_tubeLength(3);
        link[5][2].setDistance(3000);
        link[5][2].setCongestionRate(INIT_RATE);
        adjMatrix[5][2] = 1;

        link[3][5].setLink(beaconList.getBeacon(3), beaconList.getBeacon(5), 20);
        link[3][5].setD_tubeThickness(INIT_THICKNESS);
        link[3][5].setL_tubeLength(2);
        link[3][5].setDistance(2000);
        link[3][5].setCongestionRate(INIT_RATE);
        adjMatrix[3][5] = 1;

        link[5][3].setLink(beaconList.getBeacon(5), beaconList.getBeacon(3), 20);
        link[5][3].setD_tubeThickness(INIT_THICKNESS);
        link[5][3].setL_tubeLength(2);
        link[5][3].setDistance(2000);
        link[5][3].setCongestionRate(INIT_RATE);
        adjMatrix[5][3] = 1;

        link[4][5].setLink(beaconList.getBeacon(4), beaconList.getBeacon(5), 20);
        link[4][5].setD_tubeThickness(INIT_THICKNESS);
        link[4][5].setL_tubeLength(3);
        link[4][5].setDistance(3000);
        link[4][5].setCongestionRate(INIT_RATE);
        adjMatrix[4][5] = 1;

        link[5][4].setLink(beaconList.getBeacon(5), beaconList.getBeacon(4), 20);
        link[5][4].setD_tubeThickness(INIT_THICKNESS);
        link[5][4].setL_tubeLength(3);
        link[5][4].setDistance(3000);
        link[5][4].setCongestionRate(INIT_RATE);
        adjMatrix[5][4] = 1;

    }

    public void nodeConfigureToPajek(String NET_file, Client client, BeaconCluster beaconList) {
        double maxDistance = Math.sqrt(2);  // 最大距離 sqrt(2)

        //sourceとdistを取得
        Beacon source = client.getFlow().getSource();
        Beacon dist = client.getFlow().getDestination();

        sourceCluster.add(source.getId());
        distCluster.add(dist.getId());


        // ファイル出力処理
        try (FileWriter writer = new FileWriter(new File(NET_file))) {
            writer.write("*Vertices\t" + node + "\n");
            for (int i = 0; i < node; i++) {
                for(int j=0; j<sourceCluster.size(); j++){
                    if(i == sourceCluster.get(j)){
                        fig_SOURCE = true;
                    }
                }
                for(int j=0; j<distCluster.size(); j++){
                    if(i == distCluster.get(j)){
                        fig_DIST = true;
                    }
                }
                if (fig_SOURCE || fig_DIST) {
                    writer.write(String.format("%d \"%d\" %.4f %.4f ic Black\n", i + 1, i + 1, beaconList.getBeacon(i).getX(), beaconList.getBeacon(i).getY()));
                } else {
                    writer.write(String.format("%d \"%d\" %.4f %.4f ic White\n", i + 1, i + 1, beaconList.getBeacon(i).getX(), beaconList.getBeacon(i).getY()));
                }
                fig_SOURCE = false;
                fig_DIST = false;
            }
            writer.write("*Arcs\n*Edges\n");

            /**
             //nodeDistanceで接続を決定
             for (int i = 0; i < node; i++) {
             for (int j = 0; j < node; j++) {

             if (i == j) {
             //nothing to do
             } else {
             double distance = link.get(i).get(j).getDistance();

             if (0.0 < distance && distance <= THRESHOLD_1) {
             writer.write(String.format("%d %d 1\n", i + 1, j + 1));
             } else if (THRESHOLD_1 < distance && distance <= THRESHOLD_2) {
             writer.write(String.format("%d %d 1\n", i + 1, j + 1));
             } else if (THRESHOLD_2 < distance && distance <= maxDistance) {
             writer.write(String.format("%d %d 1\n", i + 1, j + 1));
             }
             }
             }
             }
             */
            for(int i = 0; i < node; i++){
                for(int j = 0; j < node; j++){
                    if(i != j && link[i][j].getL_tubeLength() != INF){
                        writer.write(String.format("%d %d 1\n", i + 1, j + 1));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // setTopologyColorメソッドの追加
    public void outputToPajek(Client client, double eps, double Q_allFlow, int ct) throws IOException {

        Beacon source = client.getFlow().getSource();
        Beacon dist = client.getFlow().getDestination();

        // ディレクトリパスを作成
        String dirPath = "src/result/pajek/result" + num_pajek;
        // ファイル名を作成
        String filename = dirPath + "/test_topology_" + (ct + 1) + ".net";

        // Fileオブジェクトでディレクトリの存在を確認・作成
        File dir = new File(dirPath);
        if (!dir.exists()) {
            // ディレクトリが存在しない場合は作成
            dir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("*Vertices\t" + node + "\n");
            for (int i = 0; i < node; i++) {
                if (i == source.getId() || i == dist.getId()) {
                    writer.write(String.format("%d \"%d\" %.4f %.4f ic Black\n", i + 1, i + 1, source.getX(), source.getY()));
                } else {
                    writer.write(String.format("%d \"%d\" %.4f %.4f ic White\n", i + 1, i + 1, beaconCluster.getBeacon(i).getX(), beaconCluster.getBeacon(i).getY()));
                }
            }
            writer.write("*Arcs\n*Edges\n");

            for (int i = 0; i < node; i++) {
                for (int j = 0; j < node; j++) {
                    if (link[i][j].getL_tubeLength() != INF){
                        double flow = link[i][j].getQ_tubeFlow();
                        if (0 < link[i][j].getDistance() && link[i][j].getDistance() <= Math.sqrt(2)) {
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
    //Excelファイルに各リンクの流量を出力するメソッド
    public void outputToExcel(Client client, int ct) throws IOException {

        // ディレクトリパスを作成
        String dirPath = "src/result/excel/result" + num_excel;
        // ファイル名を作成
        String filename = dirPath + "/test_topology_" + (ct + 1) + ".net";

        // Fileオブジェクトでディレクトリの存在を確認・作成
        File dir = new File(dirPath);
        if (!dir.exists()) {
            // ディレクトリが存在しない場合は作成
            dir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("source,destination,flow\n");
            for (int i = 0; i < node; i++) {
                for (int j = 0; j < node; j++) {
                    if (link[i][j].getL_tubeLength() != INF) {
                        writer.write(String.format("%d,%d,%.4f\n", i, j, link[i][j].getQ_tubeFlow()));
                    }
                }
            }
        }
    }
    //txtファイルに管の長さ，管の太さ，管の容量を出力するメソッド
    public void outputToTxt(Client client, int ct) throws IOException {
        // ディレクトリパスを作成
        String dirPath = "src/result/txt/result" + num_txt;
        // ファイル名を作成
        String filename = dirPath + "/test_topology_" + (ct + 1) + ".net";

        // Fileオブジェクトでディレクトリの存在を確認・作成
        File dir = new File(dirPath);
        if (!dir.exists()) {
            // ディレクトリが存在しない場合は作成
            dir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(filename)) {
            //要求uav台数，出発ノード，到着ノードを１行目に出力
            writer.write(String.format("%.1f,%d,%d\n", client.getFlow().getTheNumberOfUAV(), client.getFlow().getSource().getId(), client.getFlow().getDestination().getId()));
            writer.write("source,destination,length,thickness,capacity\n");
            for (int i = 0; i < node; i++) {
                for (int j = 0; j < node; j++) {
                    if (link[i][j].getL_tubeLength() != INF) {
                        writer.write(String.format("%d,%d,%.4f,%.4f,%.4f\n", i, j, link[i][j].getL_tubeLength(), link[i][j].getD_tubeThickness(), link[i][j].getCapacity()));
                    }
                }
            }
        }
    }


    //UAVを移動させるメソッド
    public void flyUAV(Client client) {
        /**clientが保持する1台1台のUAV位置を更新する
         * clientが保持するUAVTimerとUAVが持つ速さを掛け算することでUAVの移動距離がわかる
         * UAVの移動経路はUAVのpathに格納されているのでそれを参照する
         * リンク飛行中のUAV数をカウントする配列を用意し，updateCapacity()により管の容量を更新するメソッドを呼び出す
         **/
        int nodeExcept = node - 1;
        int[] UAV_count = new int[nodeExcept];
        for (int i = 0; i < nodeExcept; i++) {
            UAV_count[i] = 0;
        }

        for (int i = 0; i < client.getFlow().getTheNumberOfUAV(); i++) {
            int pathSize = client.getFlow().getUav(i).getPath().size();
            if (pathSize > 1) {
                int source = client.getFlow().getUav(i).getPath().get(0);
                int dist = client.getFlow().getUav(i).getPath().get(1);
                link[source][dist].setCongestionRate(link[source][dist].getCongestionRate() + 1);
                UAV_count[dist - 1]++;
            }
        }

        for (int i = 0; i < node; i++) {
            for (int j = 0; j < node; j++) {
                if (link[i][j].getL_tubeLength() != INF) {
                    link[i][j].setCongestionRate(link[i][j].getCongestionRate() / (1 + UAV_count[j - 1]));
                }
            }
        }

    }

    //管の容量を更新するメソッド
    public void updateCapacity() {
        for (int i = 0; i < node; i++) {
            for (int j = 0; j < node; j++) {
                if (link[i][j].getL_tubeLength() != INF) {
                    link[i][j].setCapacity(link[i][j].getD_tubeThickness() * link[i][j].getL_tubeLength());
                }
            }
        }
    }

    //PSを実行するメソッド
    public void run(Client client, int numLoop) throws IOException {
        int nodeExcept = node - 1;
        int ct = 0;
        double eps = 1e-10;
        int testIter = 10;
        int a=0, b, i, j;
        double degeneracyEffect = 1.0;

        if(runCounter != 0){
            //更新メソッドを呼び出す
            reset();
            flyUAV(client);
            updateCapacity();
        }

        while (ct < numLoop) {
            //sourceとdistを取得
            Beacon source = client.getFlow().getSource();
            Beacon dist = client.getFlow().getDestination();
            Q_Kirchhoff[source.getId()] = client.getFlow().getTheNumberOfUAV();
            Q_Kirchhoff[dist.getId()] = client.getFlow().getTheNumberOfUAV() * NEG;
            sourceCluster.add(source.getId());
            distCluster.add(dist.getId());


            for(i=0; i<node; i++){
                pressureCoefficient[i][i] = 0.0;  // i番目の行、i番目の列に0.0を設定
                for(j=0; j< sourceCluster.size(); j++){
                    for(int k=0; k< distCluster.size(); k++){
                        if(i == sourceCluster.get(j) || i == distCluster.get(k)){
                            fig_DIST = true;
                        }
                    }
                }
                if(!fig_DIST){
                    Q_Kirchhoff[i] = 0.0;

                }
                fig_DIST = false;
            }

            // 圧力勾配の導出
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (link[i][j].getL_tubeLength() != INF){//ノードiとノードjが直接接続されている場合
                        if (i != j) { // iとjが異なる場合
                            pressureCoefficient[i][j] = link[i][j].getD_tubeThickness() / link[i][j].getL_tubeLength() * NEG;// 圧力係数を計算
                        }
                    }
                }
            }

            int k = 0;
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (link[i][j].getL_tubeLength() != INF) { // ノードiとノードjが直接接続されている場合
                        pressureCoefficient[k][k] = pressureCoefficient[k][k] + link[i][j].getD_tubeThickness() / link[i][j].getL_tubeLength();// 対角成分を加算
                    }
                }
                k++;
            }

             if(BiCGSTAB.BiCGSTAB(pressureCoefficient, Q_Kirchhoff, P_tubePressure, node, testIter, eps) == 0){
                break;
             }

            // 流量の計算
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (link[i][j].getL_tubeLength() != INF) {
                        link[i][j].setQ_tubeFlow((link[i][j].getD_tubeThickness() / link[i][j].getL_tubeLength()) * (P_tubePressure[i] - P_tubePressure[j]));
                    }
                }
            }

            // シグモイド関数
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (link[i][j].getL_tubeLength() != INF) {
                        Q_tubeFlow_sigmoidOutput[i][j] = Math.pow(Math.abs(link[i][j].getQ_tubeFlow()), GAMMA) / (1 + Math.pow(Math.abs(link[i][j].getQ_tubeFlow()), GAMMA));

                    }
                }
            }

            // チューブ厚の更新
            for (i = 0; i < node; i++) {
                for (j = 0; j < node; j++) {
                    if (link[i][j].getL_tubeLength() != INF) {
                        double deltaThickness = (Math.abs(link[i][j].getQ_tubeFlow()) - (degeneracyEffect * link[i][j].getD_tubeThickness())) * DELTA_TIME;
                        D_tubeThickness_deltaT[i][j] = deltaThickness;

                    }
                }
            }

            for(i=0; i<node; i++){
                for(j=0; j<node; j++){
                    {
                        link[i][j].setD_tubeThickness(link[i][j].getD_tubeThickness() + (D_tubeThickness_deltaT[i][j]) * Math.tanh((link[i][j].getCapacity() - Math.abs(link[i][j].getQ_tubeFlow())) * coefficient_tanh));
                    }
                }
            }
            // 結果のプロット
            if ((ct + 1) % PLOT == 0) {
                System.out.println("Iteration: " + (ct+1));
                outputToPajek(client, eps, client.getFlow().getTheNumberOfUAV(), ct);
                outputToExcel(client, ct);
                outputToTxt(client, ct);
            }

            ct++;
            // 最後のループの場合に実行する処理
            // UAV一台ずつに経路を配列として受け渡し、飛行経路をすべてのUAVに割り当てる
            if (ct == numLoop) {
                // 初期設定として、Flow_CapacityにQ_tubeFlowを代入
                for (i = 0; i < node; i++) {
                    for (j = 0; j < node; j++) {
                        Flow_Capacity[i][j] = link[i][j].getQ_tubeFlow();
                    }
                }

                // 各リンクを流れる流量の整数値をtubeFlowに追加
                for (i = 0; i < node; i++) {
                    for (j = 0; j < node; j++) {
                        int flow = (int) Math.floor(Flow_Capacity[i][j]);
                        tubeFlow[i][j] = flow;
                    }
                }


                // スタートノード、ゴールノード、必要なUAV台数を取得
                int startNode = client.getFlow().getSource().getId();
                int goalNode = client.getFlow().getDestination().getId();
                int requiredUAVs = (int) client.getFlow().getTheNumberOfUAV();

                // 実際のUAVに経路を割り当てるためのメイン処理
                runUAVFlow(startNode, goalNode, requiredUAVs, client);
            }
        }
        //client.startTimer();
        runCounter++;
        reset();
    }


    // UAVの経路を探索し、各UAVに経路を割り当てるメソッド
    public void runUAVFlow(int startNode, int goalNode, int requiredUAVs, Client client) {
        UAV_count = 0; // ゴールに到達したUAVの数を追跡

        // 全UAV分の経路を格納するリスト
        List<ArrayList<Integer>> paths = new ArrayList<>(requiredUAVs);

        // 要求UAV数に到達するまで経路探索を繰り返す
        while (UAV_count < requiredUAVs) {
            int previousUAVCount = UAV_count;
            min_Flow = 100;

            // 新しい経路を格納するリストを初期化し、スタートノードを追加
            ArrayList<Integer> path = new ArrayList<>(5);
            path.add(startNode);

            // スタートノードから経路を再帰的に探索し、成功時にUAV_countを増加
            int flow = 0;
            flow = explorePath(startNode, startNode, startNode, goalNode, path, flow); // 流量（UAV台数）を取得

            if (flow > 0) {
                // 見つかった経路を `flow` 回 `paths` に追加
                for (int f = 0; f < flow; f++) {
                    paths.add(new ArrayList<>(path));
                    client.getFlow().getUav(UAV_count + f).setPath(path);
                    client.getFlow().getUav(UAV_count + f).startTimer();
                }
                UAV_count += flow;  // UAV_countを流量分増加
            } else {
                //変更する必要がある
                if (previousUAVCount == UAV_count && UAV_count < requiredUAVs) {
                    int needUAV = requiredUAVs - UAV_count;
                    adjustRemainingFlow(needUAV, startNode, goalNode, client);
                    break;
                }
            }

            if(UAV_count == requiredUAVs) break;
        }

        // 全UAVに経路を割り当てられなかった場合の警告
        if (UAV_count < requiredUAVs) {
            System.out.println("全てのUAVに経路が割り当てられませんでした");
        }
    }

    /**
     * 再帰的な経路探索でリンクを辿り、経路を記録するメソッド（DFS）
     * 探索が成功したら流量（UAV数）を返し、pathリストに経路を追加
     */
    private int explorePath(int startNode, int passedNode, int currentNode, int goalNode, ArrayList<Integer> path, int passedFlow) {
        // ゴールノードに到達したら流量を返して経路探索を終了
        if (currentNode == goalNode) {
            return passedFlow;
        }

        // 次のノードを探索し、経路を進む
        for (int nextNode = 0; nextNode < node; nextNode++) {
            if (adjMatrix[currentNode][nextNode] == 1 && tubeFlow[currentNode][nextNode] > 0) {
                int flow = tubeFlow[currentNode][nextNode]; // 現在ノード間の流量

                // 最小フローの計算
                if (passedFlow == 0) {
                    min_Flow = flow;
                } else {
                    min_Flow = Math.min(min_Flow, flow);
                }

                // 経路に次のノードを追加
                path.add(nextNode);

                // 最終的に見つかった経路に沿ってフローを減少させる
                if (nextNode == goalNode && min_Flow > 0) {
                    int nodeA = startNode;
                    for (int nodeB : path) {
                        // `tubeFlow` と `Flow_Capacity` を減算
                        tubeFlow[nodeA][nodeB] = tubeFlow[nodeA][nodeB] - min_Flow;
                        Flow_Capacity[nodeA][nodeB] = Flow_Capacity[nodeA][nodeB] - min_Flow;

                        // `tubeFlow` が0なら `adjMatrix` から接続を削除
                        if (tubeFlow[nodeA][nodeB] == 0) {
                            adjMatrix[nodeA][nodeB] = 0;
                        }
                        nodeA = nodeB;
                    }
                }
                // 再帰的に経路を探索し、成功時には流量を返す
                int resultFlow = explorePath(startNode, currentNode, nextNode, goalNode, path, min_Flow);
                if (resultFlow > 0) {
                    return resultFlow; // 見つかった最小フローを返す
                }

                // 探索が失敗した場合、経路からノードを削除（バックトラック）
                path.remove(path.size() - 1);
                min_Flow = (int) INF;  // 最小フローをリセット
            }
        }

        return 0; // 失敗した場合、流量0を返す
    }




    private void adjustRemainingFlow(int needUAV, int startNode, int goalNode, Client client) {
        int countOfUAV = 0;
        ArrayList<Integer> path = new ArrayList<>(5); // path を再利用

        while (countOfUAV < needUAV) {
            path.clear();  // 毎回新たに初期化せず再利用
            path.add(startNode);

            int currentNode = startNode;
            boolean pathFound = false;

            // 目的地に到達するまで経路を探索
            while (currentNode != goalNode) {
                int nextNode = -1;
                double maxCapacity = -1.0;

                // 6. Flow_Capacityが1以上のリンクを探索
                for (int j = 0; j < node; j++) {
                    if (tubeFlow[currentNode][j] > 0 && Flow_Capacity[currentNode][j] >= 1) {
                        if (Flow_Capacity[currentNode][j] > maxCapacity) {
                            maxCapacity = Flow_Capacity[currentNode][j];
                            nextNode = j;
                        }
                    }
                }

                // Flow_Capacityが1以上のリンクが存在しない場合、最大容量のリンクのFlow_Capacityを1に変更
                if (nextNode == -1) {
                    for (int j = 0; j < node; j++) {
                        if (Flow_Capacity[currentNode][j] > maxCapacity) {
                            maxCapacity = Flow_Capacity[currentNode][j];
                            nextNode = j;
                        }
                    }
                    if (nextNode != -1) {
                        tubeFlow[currentNode][nextNode] = 1;
                        Flow_Capacity[currentNode][nextNode] = 1.0;
                    }
                }

                // 次のノードが見つからない場合、経路が無効なので終了
                if (nextNode == -1) {
                    break;
                }

                // 選択されたリンクに沿ってtubeFlowとFlow_Capacityを減少させ、経路を進む
                path.add(nextNode);
                int flow = tubeFlow[currentNode][nextNode];
                tubeFlow[currentNode][nextNode] = tubeFlow[currentNode][nextNode] - flow;
                Flow_Capacity[currentNode][nextNode] = Flow_Capacity[currentNode][nextNode] - flow;

                currentNode = nextNode;

                // 目的地に到達した場合、経路をUAVに割り当て
                if (currentNode == goalNode) {
                    UAV_count += flow;
                    countOfUAV += flow;
                    pathFound = true;

                    // 複数のUAVが同じ経路を使用できる場合、それぞれに経路を設定
                    for (int uav = 0; uav < flow; uav++) {
                        if (uav >= needUAV) break;
                        client.getFlow().getUav(UAV_count - 1).setPath(new ArrayList<>(path));
                        client.getFlow().getUav(UAV_count - 1).startTimer();
                    }
                    break;
                }
            }

            // 経路が見つからなかった場合の処理
            if (!pathFound) {
                System.out.println("有効な経路が見つかりませんでした");
                break;
            }
        }
    }

}
