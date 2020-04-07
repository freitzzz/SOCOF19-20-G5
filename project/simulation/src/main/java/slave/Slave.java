package slave;

import datastructures.Request;
import datastructures.handler.SlaveHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Slave {

    //declare variables
    private int performanceIndex;
    private double availability;
    private SlaveHandler slaveHandler;
    private final Executor exec;

    //implement setters and getters
    public int getPerformanceIndex() {
        return performanceIndex;
    }

    public double isAvailability() {
        return availability;
    }

    private void setAvailability(double availability) {
        this.availability = availability;
    }

    //add constructor
    public Slave(int performanceIndex, double availability, SlaveHandler slaveHandler){
        this.performanceIndex = performanceIndex;
        this.availability = availability;
        this.slaveHandler = slaveHandler;
        this.exec = Executors.newFixedThreadPool(performanceIndex);
    }

    public void compute(Request request){
        Runnable task = new ComputeThread(request,slaveHandler);
        exec.execute(task);
    }

}
