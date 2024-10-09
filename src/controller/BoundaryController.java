package controller;
import client.Client;
import client.ClientController;
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
    static Client client;
    static ClientController clientController;

    String filePath = "src/result/practice.net";

    //ネットワークトポロジーを設定する関数
    public void setNetworkTopology() throws IOException {
        //ビーコンクラスタークラスを取得
        beaconList = beaconCluster.getBeaconList();
        //ビーコンの情報を設定
        setLink();
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

    //クライアントを生成する関数
    public Client createClient(){
        Random random = new Random();
        int sourceId = random.nextInt(nodeNum);
        int distinationId = random.nextInt(nodeNum);
        while (sourceId == distinationId){
            distinationId = random.nextInt(nodeNum);
        }
        Beacon source = beaconCluster.getBeacon(sourceId);
        Beacon distination = beaconCluster.getBeacon(distinationId);
        int uavNum = random.nextInt(10);
        Client client = new Client(source, distination, uavNum);
        clientController.addClient(client);

        return client;
    }

    public void routeRequest(Client client){
        //PSを実行
        solver.routeRequest(client);
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
