package client;

import item.*;

import java.util.ArrayList;

public class Client {
    private ArrayList<Flow> flow;

    private int finishFlyingCounter = 0;

    public Client(ArrayList<Flow> flow) {
        this.flow = flow;
        for(Flow f : flow) {
            createUav(f);
        }
    }

    //UAV数だけUAVを生成
    public void createUav(Flow flow) {
        ArrayList<Uav> uavList= new ArrayList<>((int) flow.getTheNumberOfUAV());
        //UAV数だけUAVを生成
        for (int i = 0; i < flow.getTheNumberOfUAV(); i++) {
            Uav uav = new Uav(1, flow.getSource().getX(), flow.getSource().getY(), i, flow.getSource(), flow.getDestination());
            uavList.add(uav);
        }
        flow.setUavList(uavList);
    }

    public int getFinishFlyingCounter() {
        return finishFlyingCounter;
    }

    public void setFinishFlyingCounter(int finishFlyingCounter) {
        this.finishFlyingCounter = finishFlyingCounter;
    }

    public void incrementFinishFlyingCounter() {
        finishFlyingCounter++;
    }

    public ArrayList<Flow> getFlowList() {
        return flow;
    }

    //要素数番目のFlowを返す
    public Flow getFlow(int i) {
        return flow.get(i);
    }

}
