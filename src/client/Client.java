package client;

import item.*;

import java.util.ArrayList;

public class Client {
    private Beacon source;
    private Beacon destination;
    private double theNumberOfUAV;
    private ArrayList<Uav> uavList;
    private int finishFlyingCounter = 0;

    public Client(Beacon source, Beacon destination, int TheNumberOfUAV) {
        this.source = source;
        this.destination = destination;
        this.theNumberOfUAV = TheNumberOfUAV;
        uavList = new ArrayList<>(TheNumberOfUAV);
        createUav();
    }

    //UAV数だけUAVを生成
    public void createUav() {
        for (int i = 0; i < theNumberOfUAV; i++) {
            Uav uav = new Uav(1, source.getX(), source.getY(), i, source, destination);
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

    public Beacon getSource() {
        return source;
    }

    public Beacon getDestination() {
        return destination;
    }

    public double getTheNumberOfUAV() {
        return theNumberOfUAV;
    }
}
