package item;

import java.util.ArrayList;

public class Flow {
    Beacon source;
    Beacon destination;
    double theNumberOfUAV;
    private ArrayList<Uav> uavList;

    public Flow(Beacon source, Beacon destination, double theNumberOfUAV) {
        this.source = source;
        this.destination = destination;
        this.theNumberOfUAV = theNumberOfUAV;
    }

    public void setUavList(ArrayList<Uav> uavList) {
        this.uavList = uavList;
    }

    public ArrayList<Uav> getUavList() {
        return uavList;
    }
    //i番目のuavを返す
    public Uav getUav(int i) {
        return uavList.get(i);
    }

    public Beacon getSource() {
        return source;
    }

    public Beacon getDestination() {
        return destination;
    }

    public double getTheNumberOfUAV() {
        return theNumberOfUAV;
    }

}
