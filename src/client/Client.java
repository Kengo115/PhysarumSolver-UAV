package client;

import item.*;

import java.util.ArrayList;
import java.util.Random;

public class Client {
    private Flow flow;
    private UAVTimer uavTimer;

    private int finishFlyingCounter = 0;

    public Client(Flow flow) {
        this.flow = flow;
        createUav(flow);

    }

    //UAV数だけUAVを生成
    public void createUav(Flow flow) {
        Uav[] uavList= new Uav[((int) flow.getTheNumberOfUAV())];
        //UAV数だけUAVを生成
        for (int i = 0; i < flow.getTheNumberOfUAV(); i++) {
            Random random = new Random();
            double speed = 8 + (random.nextDouble() * 8);  // 8~16の範囲に設定
            Uav uav = new Uav(speed, flow.getSource().getX(), flow.getSource().getY(), i, flow.getSource(), flow.getDestination());
            uavList[i] = uav;
        }

        flow.setUavList(uavList);
    }

    public void startTimer(){
        uavTimer = new UAVTimer();
        uavTimer.start();
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

    public Flow getFlow() {
        return flow;
    }

}
