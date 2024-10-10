package item;

public class Beacon {
    private double x;
    private double y;
    private int id;
    private double Q_Kirchhoff;
    private double P_tubePressure;

    //コンストラクタ
    public Beacon(double x, double y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
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
    public Beacon getBeacon() {
        return this;
    }
}
