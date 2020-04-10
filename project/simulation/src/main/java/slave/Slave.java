package slave;

import datastructures.Request;
import datastructures.handler.SlaveHandler;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Slave implements Comparable<Slave> {

    //declare variables
    private int performanceIndex;
    private AtomicInteger availability;
    private final Executor exec;

    //implement setters and getters
    public int getPerformanceIndex() {
        return performanceIndex;
    }

    public AtomicInteger getAvailability() {
        return availability;
    }

    //add constructor
    public Slave(int performanceIndex){
        this.performanceIndex = performanceIndex;
        this.availability = new AtomicInteger(100);
        this.exec = Executors.newFixedThreadPool(performanceIndex);
    }

    public void compute(Request request, SlaveHandler slaveHandler){
        Runnable task = new ComputeThread(request,slaveHandler,this);
        exec.execute(task);
    }

    public int getAvailabilityReducePerCompute() {
        return 25; // this may vary
    }

    @Override
    public int compareTo(Slave slave) {
        return Integer.compare(this.getPerformanceIndex(), slave.getPerformanceIndex());
    }
}
