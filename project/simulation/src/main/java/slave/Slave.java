package slave;

import datastructures.Request;
import datastructures.handler.SlaveHandler;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Slave {

    //declare variables
    private int performanceIndex;
    private AtomicReference<Double> availability;
    private SlaveHandler slaveHandler;
    private final Executor exec;

    //implement setters and getters
    public int getPerformanceIndex() {
        return performanceIndex;
    }

    public AtomicReference<Double> getAvailability() {
        return availability;
    }

    //add constructor
    public Slave(int performanceIndex, SlaveHandler slaveHandler){
        this.performanceIndex = performanceIndex;
        this.availability = new AtomicReference<>(100d);
        this.slaveHandler = slaveHandler;
        this.exec = Executors.newFixedThreadPool(performanceIndex);
    }

    public void compute(Request request){
        Runnable task = new ComputeThread(request,slaveHandler);
        exec.execute(task);
    }

    public void reserveWork() {

        //throw new NotImplementedException();

    }

}
