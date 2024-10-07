package controller;
import item.*;
import server.PhysarumSolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class BoundaryController {
    private static int nodeNum;
    //ビーコンクラスタークラスを生成
    static BeaconCluster beaconCluster;
    public ArrayList<Beacon> beaconList = new ArrayList<>();
    static PhysarumSolver solver;
    String filePath = "src/result/practice.net";

    //ネットワークトポロジーを設定する関数
    public void setNetworkTopology() throws IOException {
        //ビーコンクラスタークラスを取得
        beaconList = beaconCluster.getBeaconList();
        //ビーコンの情報を設定
        setLink();
    }

    private void setBeacon() {
        Random random = new Random();
        for (int i = 0; i < nodeNum; i++) {
            //ビーコンの座標を設定
            beaconList.get(i).setPos(random.nextDouble(), random.nextDouble());
            //ビーコンのIDを設定
            beaconList.get(i).setId(i);
        }
    }

    private void setLink() throws IOException {
        solver.linkConfigure(filePath, nodeNum, beaconCluster);
    }

    public void setNodeNum(int nodeNum){
        this.nodeNum = nodeNum;
    }

    public int getNodeNum() {
        return nodeNum;
    }

    public static void main(String[] args) {
        BoundaryController boundaryController = new BoundaryController();
        boundaryController.setNodeNum(10);
        solver = new PhysarumSolver(nodeNum);
        beaconCluster = new BeaconCluster(nodeNum);

        try {
            boundaryController.setNetworkTopology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
