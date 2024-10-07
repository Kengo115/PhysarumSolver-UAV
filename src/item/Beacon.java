package item;

public class Beacon {
    private double x;
    private double y;
    private int id;

    //コンストラクタ
    public Beacon(double x, double y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
    //座標を設定する
    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }
    //idを設定する
    public void setId(int id) {
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
