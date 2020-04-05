package main.java;

public class Node {

    //declare variables
    public int performanceIndex;
    public boolean availability;

    //implement setters and getters
    public int getPerformanceIndex() {
        return performanceIndex;
    }

    public void setPerformanceIndex(int performanceIndex) {
        this.performanceIndex = performanceIndex;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    //add constructor
    public Node(int performanceIndex, boolean availability){
        this.performanceIndex = performanceIndex;
        this.availability = availability;
    }

}
