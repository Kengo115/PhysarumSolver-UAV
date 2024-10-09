package client;

import item.*;

import java.util.ArrayList;

public class Client {
    private Beacon Source;
    private Beacon Distination;
    private int TheNumberOfUAV;
    private ArrayList<Uav> uavList;
    private int finishFlyingCounter = 0;

    public Client(Beacon Source, Beacon Distination, int TheNumberOfUAV) {
        this.Source = Source;
        this.Distination = Distination;
        this.TheNumberOfUAV = TheNumberOfUAV;
        uavList = new ArrayList<>(TheNumberOfUAV);
        createUav();
    }

    //UAV数だけUAVを生成
    public void createUav() {
        for (int i = 0; i < TheNumberOfUAV; i++) {
            Uav uav = new Uav(1, Source.getX(), Source.getY(), i, Source, Distination);
            uavList.add(uav);
        }
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
}
