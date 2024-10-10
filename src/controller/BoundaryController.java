package controller;
import client.Client;
import client.ClientController;
import item.*;
import server.PhysarumSolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class BoundaryController {
    private static int num_loop = 2000;
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

    private void setLink(){
        solver.setLink(nodeNum, beaconCluster);
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
        int destinationId = random.nextInt(nodeNum);
        while (sourceId == destinationId){
            destinationId = random.nextInt(nodeNum);
        }
        Beacon source = beaconCluster.getBeacon(sourceId);
        Beacon destination = beaconCluster.getBeacon(destinationId);
        int uavNum = random.nextInt(10);
        Client client = new Client(source, destination, uavNum);
        clientController.addClient(client);

        return client;
    }

    public void routeRequest(Client client){
        //PSを実行
        solver.nodeConfigureToPajek(filePath, client, beaconCluster);
        solver.run(client, num_loop);
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
