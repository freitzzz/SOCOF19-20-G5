package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import master.Master;
import slave.Slave;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import datastructures.scheduler.SlaveScheduler;

public class LockBasedSlaveHandler extends SlaveHandler{

    private List<Result> results = new ArrayList<>();
    ReentrantLock lock = new ReentrantLock();
    private LinkedList<Slave> slaves = new LinkedList<Slave>();
    private Map<Integer, LinkedList<Result>> computationResults = new HashMap<>();


    public LockBasedSlaveHandler(final SlaveScheduler scheduler, final Master master, final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves.addAll(slaves);
    }


    @Override
    public void requestComputation(Request request) {
        int slaveAvailabilityAfterCompute = 0;
        int currentSlaveAvailability = 0;
        List<Slave> availableSlaves = new ArrayList<>();

        lock.lock();
        try{
            for(Slave slave : slaves){
                currentSlaveAvailability  = slave.getAvailability().get();
                slaveAvailabilityAfterCompute = currentSlaveAvailability - slave.getAvailabilityReducePerCompute();
                if(slaveAvailabilityAfterCompute >= 0) {
                    //add slave to availableSlaves List
                    availableSlaves.add(slave);
                }
            }
            //if availableSlaves > 0 then computationResults.put(request.getRequestID(), new LinkedList<>()) and scheduler.schedule(availableSlaves, request)
            if(availableSlaves.size() > 0){
                computationResults.put(request.getRequestID(), new LinkedList<>());
                super.scheduler.schedule(availableSlaves, request);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void pushResult(Result result) {
        this.results.add(result);
    }

    @Override
    public void reportPerformance(Slave slave, int index) {

    }

    @Override
    public void reportAvailability(Slave slave, double availability) {

    }
}