package item;

public class Uav {
    private int speed;
    private double x;
    private double y;
    private int id;
    private Beacon Source;
    private Beacon Destination;

    //コンストラクタ
    public Uav(int speed, double x, double y, int id, Beacon Source, Beacon Destination) {
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.id = id;
        this.Source = Source;
        this.Destination = Destination;
    }

    //速度を返す
    public int getSpeed() {
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
