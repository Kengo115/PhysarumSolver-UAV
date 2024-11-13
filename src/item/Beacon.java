package item;

public class Beacon {
    private double x;
    private double y;
    private int id;

    public Beacon(double x, double y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public int getId(){
        return id;
    }

    public Beacon getBeacon(){
        return this;
    }
}
