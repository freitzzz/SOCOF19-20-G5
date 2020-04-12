package datastructures.handler;

import datastructures.*;
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


    public LockBasedSlaveHandler(final SlaveScheduler scheduler, final Master master,  final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves = new LockBasedList<>(slaves.size());
        this.slaves.addAll(slaves);
    }


    @Override
    public void requestSlaves(Request request) {
        int slaveAvailabilityAfterCompute = 0;
        int currentSlaveAvailability = 0;
        List<Slave> availableSlaves = new ArrayList<>();

        lock.lock();
        try{
            for(Slave slave : slaves){
                currentSlaveAvailability  = slave.getAvailability().get();
                slaveAvailabilityAfterCompute = currentSlaveAvailability - slave.getAvailabilityReducePerCompute(request);
                if(slaveAvailabilityAfterCompute >= 0) {
                    availableSlaves.add(slave);
                }
            }
            if(availableSlaves.size() > 0){

                if(request instanceof CodeExecutionRequest) {
                    computationResults.put(request.getRequestID(), new LockBasedList<>(availableSlaves.size()));
                    super.scheduler.schedule(availableSlaves, (CodeExecutionRequest) request, this);
                } else {
                    slaves.forEach(slave -> slave.process(request, this));
                }
            } else {
                super.master.receiveRequestCouldNotBeScheduled(request);
            }
        } finally {
            System.out.println("Request added" + Thread.currentThread().getName());
            lock.unlock();
        }
    }

    @Override
    public void pushResult(Result result) {
        final LockBasedList<Result> results = computationResults.get(result.getRequestID());
        results.add(result);
        if (results.hasReachedMaxSize()) {
            final int finalResultSum;
            Result finalResult;
            switch(result.getOperation()){
                case ADD:
                    finalResultSum = results.parallelStream().mapToInt(Result::getValue).sum();
                    finalResult = new Result(finalResultSum, result.getRequestID(),result.getOperation());
                    break;
                case MULTIPLY:
                    finalResultSum = results.parallelStream().mapToInt(Result::getValue).reduce(1,(i, i1) -> i*i1);
                    finalResult = new Result(finalResultSum, result.getRequestID(),result.getOperation());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation");
            }
            computationResults.remove(result.getRequestID());
            super.master.receiveResult(finalResult);
        }
    }

    @Override
    public void reportPerformance(Slave slave) {
        final PerformanceDetails details = new PerformanceDetails(slave, slave.getPerformanceIndex());
        super.master.receiveSlavePerformanceDetails(details);
    }

    @Override
    public void reportAvailability(Slave slave, Request request) {
        int slaveAvailabilityAfterCompute = 0;
        int currentSlaveAvailability = 0;
        lock.lock();
        try {
            currentSlaveAvailability  = slave.getAvailability().get();
            slaveAvailabilityAfterCompute = currentSlaveAvailability + slave.getAvailabilityReducePerCompute(request);
            if(slaveAvailabilityAfterCompute <= 100) {
                final AvailabilityDetails details = new AvailabilityDetails(slave, slave.getAvailability().intValue());
                super.master.receiveSlaveAvailability(details);
            }

        } finally {
            lock.unlock();
        }
    }
}