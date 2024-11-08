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
    private ArrayList<Integer> path;

    //コンストラクタ
    public Uav(double speed, double x, double y, int id, Beacon Source, Beacon Destination) {
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.id = id;
        this.Source = Source;
        this.Destination = Destination;
    }
    public void setPath(ArrayList<Integer> path) {
        this.path = path;
    }

    public ArrayList<Integer> getPath() {
        return path;
    }

    public void startTimer(){
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
        uavTimer.cancel();
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
}
