package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import datastructures.list.LockFreeList;
import datastructures.map.LockFreeMap;
import datastructures.scheduler.SlaveScheduler;
import slave.Slave;

import java.util.List;
import java.util.stream.Collectors;

public class LockFreeSlaveHandler extends SlaveHandler {

    private final LockFreeList<Slave> slaves = new LockFreeList<>();

    private final LockFreeMap<Integer, LockFreeList<Result>> computationResults = new LockFreeMap<>();

    public LockFreeSlaveHandler(final SlaveScheduler scheduler, final List<Slave> slaves) {
        super(scheduler);
        this.slaves.addAll(slaves);
    }

    @Override
    public void requestComputation(final Request request) {
        List<Slave> availableSlaves = slaves
                .parallelStream()
                .filter(this::tryReserveSlaveAvailability)
                .collect(Collectors.toList());
        if(availableSlaves.size() > 0) {
            computationResults.put(request.getRequestID(), new LockFreeList<>());
            super.scheduler.schedule(availableSlaves, request);
        } else {
            // TODO: If no slaves were reserved for scheduled than we need to announce the master that no slaves are
            // available
        }
    }

    @Override
    public void pushResult(Result result) {

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
