package item;

import java.util.ArrayList;

public class Uav {
    private double speed;
    private double x;
    private double y;
    private int id;
    private Beacon Source;
    private Beacon Destination;
    private UAVTimer uavTimer = new UAVTimer();
    private int[] path;
    private boolean isFlying = false;
    private double flightTime = 0;

    //コンストラクタ
    public Uav(double speed, double x, double y, int id, Beacon Source, Beacon Destination) {
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.id = id;
        this.Source = Source;
        this.Destination = Destination;
    }
    public void setPath(int[] path) {
        this.path = path;
    }

    public int[] getPath() {
        return path;
    }

    public void startTimer(){
        isFlying = true;
        uavTimer.start();
    }

    public void stopTimer(){
        uavTimer.stop();
    }

    public long getFlightTime(){
        return uavTimer.getFlightTime();
    }

    public void resetTimer() {
        uavTimer.reset();
    }

    public void cancelTimer() {
        isFlying = false;
        uavTimer.cancel();
    }


    public boolean getIsFlying() {
        return isFlying;
    }
    //速度を返す
    public double getSpeed() {
        return speed;
    }
    //x座標を返す
    public double getX() {
        return x;
    }
    //y座標を返す
    public double getY() {
        return y;
    }
    //idを返す
    public int getId() {
        return id;
    }
    //出発地を返す
    public Beacon getSource() {
        return Source;
    }
    //到着地を返す
    public Beacon getDistination() {
        return Destination;
    }

    public void setFlightTime(double flightTime) {
        this.flightTime = flightTime;
    }

    public double getFlightTime(double flightTime) {
        return flightTime;
    }
}
