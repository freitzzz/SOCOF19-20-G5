package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import datastructures.list.LockFreeList;
import datastructures.map.LockFreeMap;
import datastructures.scheduler.SlaveScheduler;
import master.Master;
import slave.Slave;

import java.util.List;
import java.util.stream.Collectors;

public class LockFreeSlaveHandler extends SlaveHandler {

    private final LockFreeList<Slave> slaves;

    private final LockFreeMap<Integer, LockFreeList<Result>> computationResults = new LockFreeMap<>();

    public LockFreeSlaveHandler(final SlaveScheduler scheduler, final Master master, final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves = new LockFreeList<>(slaves.size());
        this.slaves.addAll(slaves);
    }

    @Override
    public void requestComputation(final Request request) {
        List<Slave> availableSlaves = slaves
                .parallelStream()
                .filter(this::tryReserveSlaveAvailability)
                .collect(Collectors.toList());
        if(availableSlaves.size() > 0) {
            computationResults.put(request.getRequestID(), new LockFreeList<>(availableSlaves.size()));
            super.scheduler.schedule(availableSlaves, request);
        } else {
            super.master.receiveRequestCouldNotBeScheduled(request);
        }
    }

    @Override
    public void pushResult(final Result result) {
        final LockFreeList<Result> results = computationResults.get(result.getRequestID());
        results.add(result);
        if (results.hasReachedMaxSize()) {
            final int finalResultSum = results.parallelStream().mapToInt(result1 -> result1.getValue()).sum();
            Result finalResult = new Result(finalResultSum, result.getRequestID());
            super.master.receiveResult(finalResult);
        }
    }

    @Override
    public void reportPerformance(Slave slave, int index) {

    }

    @Override
    public void reportAvailability(Slave slave, double availability) {

    }

    private boolean tryReserveSlaveAvailability(final Slave slave) {
        boolean reserved = false;
        final int currentSlaveAvailability  = slave.getAvailability().get();
        final int slaveAvailabilityAfterCompute = currentSlaveAvailability - slave.getAvailabilityReducePerCompute();
        if(slaveAvailabilityAfterCompute >= 0) {

            reserved = slave.getAvailability().compareAndSet(currentSlaveAvailability, slaveAvailabilityAfterCompute);

        }
        return reserved;
    }
}
