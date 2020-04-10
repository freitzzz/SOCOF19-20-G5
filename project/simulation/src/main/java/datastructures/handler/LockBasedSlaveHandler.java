package datastructures.handler;

import datastructures.AvailabilityDetails;
import datastructures.PerformanceDetails;
import datastructures.Request;
import datastructures.Result;
import datastructures.list.LockBasedList;
import datastructures.map.LockBasedMap;
import master.Master;
import slave.Slave;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import datastructures.scheduler.SlaveScheduler;

public class LockBasedSlaveHandler extends SlaveHandler{

    ReentrantLock lock = new ReentrantLock();

    private LockBasedList<Slave> slaves;

    private LockBasedMap<Integer, LockBasedList<Result>> computationResults = new LockBasedMap<>();


    public LockBasedSlaveHandler(final SlaveScheduler scheduler, final Master master, final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves = new LockBasedList<>(slaves.size());
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
                    //add slave to availableSlaves List if the slave is available
                    availableSlaves.add(slave);
                }
            }
            //if availableSlaves > 0 then computationResults.put(request.getRequestID(), new LockBasedList<>()) and scheduler.schedule(availableSlaves, request)
            if(availableSlaves.size() > 0){
                computationResults.put(request.getRequestID(), new LockBasedList<>(availableSlaves.size()));
                super.scheduler.schedule(availableSlaves, request);
            }
        } finally {
            System.out.println("Request added" + Thread.currentThread().getName());
            lock.unlock();
        }
    }

    @Override
    public void pushResult(Result result) {
        int finalResultSum = 0;
        final LockBasedList<Result> results = computationResults.get(result.getRequestID());
        results.add(result);
        if (results.hasReachedMaxSize()) {
            for(Result res : results){
                finalResultSum = finalResultSum + res.getValue();
            }
            Result finalResult = new Result(finalResultSum, result.getRequestID());
            super.master.receiveResult(finalResult);
        }
    }

    @Override
    public void reportPerformance(Slave slave) {

        // for demo usage only System.out.println(slave.getPerformanceIndex() + Thread.currentThread().getName());
        
        final PerformanceDetails details = new PerformanceDetails(slave, slave.getPerformanceIndex());

        super.master.receiveSlavePerformanceDetails(details);

    }

    @Override
    public void reportAvailability(Slave slave) {

        // for demo usage only System.out.println(slave.getAvailability() + Thread.currentThread().getName());

        final AvailabilityDetails details = new AvailabilityDetails(slave, slave.getAvailability().intValue());

        super.master.receiveSlaveAvailability(details);

    }
}