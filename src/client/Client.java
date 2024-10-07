package client;

import item.*;
public class Client {
    private Beacon Source;
    private Beacon Distination;
    private int TheNumberOfUAV;
    private Uav[] uav;
    private int finishFlyingCounter = 0;

    public int getFinishFlyingCounter() {
        return finishFlyingCounter;
    }

    public void setFinishFlyingCounter(int finishFlyingCounter) {
        this.finishFlyingCounter = finishFlyingCounter;
    }
}
