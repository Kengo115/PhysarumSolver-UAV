package item;

public class Beacon {
    private double x;
    private double y;
    //座標を設定する
    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }
    //x座標を返す
    public double getX() {
        return x;
    }
    //y座標を返す
    public double getY() {
        return y;
    }
}
