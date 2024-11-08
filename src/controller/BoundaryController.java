package controller;

import client.Client;
import client.ClientController;
import item.*;
import server.PhysarumSolver;

import java.io.IOException;
import java.util.ArrayList;


public class BoundaryController {
    private static int num_loop = 40;
    private static int nodeNum;
    //ビーコンクラスタークラスを生成
    static BeaconCluster beaconCluster;
    public ArrayList<Beacon> beaconList;
    static PhysarumSolver solver;
    static Client client;
    static ClientController clientController = new ClientController();;
    Flow flow;

    String filePath = "src/result/practice.net";

    //ネットワークトポロジーを設定する関数
    public void setNetworkTopology() throws IOException {
        //ビーコンクラスタークラスを取得
        beaconList = new ArrayList<>(nodeNum);
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
    public Client createClient() {
        int sourceId = 0;
        int destinationId = 5;
        Beacon source = beaconCluster.getBeacon(sourceId);
        Beacon destination = beaconCluster.getBeacon(destinationId);

        int uavNum = 20;
        //flowListにsource, destination, uavNumを格納
        flow = new Flow(source, destination, uavNum);

        Client client = new Client(flow);
        clientController = new ClientController();
        clientController.addClient(client);

        return client;
    }

    public Client createClient2(){
        int sourceId = 2;
        int destinationId = 4;
        Beacon source = beaconCluster.getBeacon(sourceId);
        Beacon destination = beaconCluster.getBeacon(destinationId);

        int uavNum = 20;
        //flowListにsource, destination, uavNumを格納
        flow = new Flow(source, destination, uavNum);

        Client client = new Client(flow);
        clientController = new ClientController();
        clientController.addClient(client);

        return client;
    }
    public void routeRequest(Client client) throws IOException {
        //PSを実行
        solver.nodeConfigureToPajek(filePath, client, beaconCluster);
        solver.run(client, num_loop);
    }

    public static void main(String[] args) {
        BoundaryController boundaryController = new BoundaryController();
        boundaryController.setNodeNum(6);
        solver = new PhysarumSolver(nodeNum);
        beaconCluster = new BeaconCluster(nodeNum);

        try {
            boundaryController.setNetworkTopology();
            client = boundaryController.createClient();
            boundaryController.routeRequest(client);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
