package item;

public class Link {
    private Beacon beacon1;
    private Beacon beacon2;
    private double capacity;
    private double flyingUAV;
    private double congestionRate;
    //管の太さ
    private double D_tubeThickness;
    //管の長さ
    private double L_tubeLength;

    //コンストラクタ
    public Link(Beacon beacon1, Beacon beacon2, double capacity, double flyingUAV, double congestionRate, double D_tubeThickness, double L_tubeLength) {
        this.beacon1 = beacon1;
        this.beacon2 = beacon2;
        this.capacity = capacity;
        this.flyingUAV = flyingUAV;
        this.congestionRate = congestionRate;
        this.D_tubeThickness = D_tubeThickness;
        this.L_tubeLength = L_tubeLength;
    }

    //接続ビーコンを返す
    public Beacon getBeacon1() {
        return beacon1;
    }
    public Beacon getBeacon2() {
        return beacon2;
    }
    //容量を返す
    public double getCapacity() {
        return capacity;
    }
    //飛行中のUAV数を返す
    public double getFlyingUAV() {
        return flyingUAV;
    }
    //混雑率を返す
    public double getCongestionRate() {
        return congestionRate;
    }
    //管の太さを返す
    public double getD_tubeThickness() {
        return D_tubeThickness;
    }
    //管の長さを返す
    public double getL_tubeLength() {
        return L_tubeLength;
    }

    //混雑率を計算する
    public void calcCongestionRate() {
        congestionRate = flyingUAV / capacity;
    }

}
