package item;

public class UAV {
    private int speed;
    private double x;
    private double y;
    private Beacon Source;
    private Beacon Distination;

    //コンストラクタ
    public UAV(int speed, double x, double y, Beacon Source, Beacon Distination) {
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.Source = Source;
        this.Distination = Distination;
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
    //出発地を返す
    public Beacon getSource() {
        return Source;
    }
    //到着地を返す
    public Beacon getDistination() {
        return Distination;
    }
}
