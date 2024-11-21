package controller;

import client.Client;
import client.ClientController;
import item.*;
import server.PhysarumSolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class BoundaryController {
    private static int num_loop = 1000;
    private static int nodeNum;
    //ビーコンクラスタークラスを生成
    static BeaconCluster beaconCluster;
    public Beacon[] beaconList;
    static PhysarumSolver solver;
    static Client client;
    static ClientController clientController = new ClientController();
    static Queue<Client> passedClient = new LinkedList<>();
    Flow flow;

    String filePath = "src/result/practice.net";

    //ネットワークトポロジーを設定する関数
    public void setNetworkTopology() throws IOException {
        //ビーコンクラスタークラスを取得
        beaconList = new Beacon[nodeNum];
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

        int uavNum = 30;
        //flowListにsource, destination, uavNumを格納
        flow = new Flow(source, destination, uavNum);

        Client client = new Client(flow);
        clientController = new ClientController();
        clientController.addClient(client);

        return client;
    }

    public Client createClient2(){
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

    public Client createClient3(){
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
        solver.run(client, passedClient, num_loop);
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
            passedClient.add(client);
            //UAVの飛行を全て終えたクライアントをdequeueする
            // 20秒待機してから次の処理に移る
            try {
                Thread.sleep(30000); // 20秒待機
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted, failed to complete wait");
            }

            client = boundaryController.createClient2();
            boundaryController.routeRequest(client);
            passedClient.add(client);

            try {
                Thread.sleep(50000); // 50秒待機
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted, failed to complete wait");
            }

            client = boundaryController.createClient3();
            boundaryController.routeRequest(client);
            passedClient.add(client);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
