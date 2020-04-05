package main.java.slave;

import main.java.datastructures.Request;

public class Slave {

    //declare variables
    public int performanceIndex;
    public double availability;

    //implement setters and getters
    public int getPerformanceIndex() {
        return performanceIndex;
    }

    public void setPerformanceIndex(int performanceIndex) {
        this.performanceIndex = performanceIndex;
    }

    public double isAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

    //add constructor
    public Slave(int performanceIndex, double availability){
        this.performanceIndex = performanceIndex;
        this.availability = availability;
    }

    private void compute(Request request){

    }

}
