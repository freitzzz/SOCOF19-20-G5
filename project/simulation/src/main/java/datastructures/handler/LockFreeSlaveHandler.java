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

    public LockFreeSlaveHandler(SlaveScheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void requestComputation(final Request request) {
        List<Slave> availableSlaves = slaves
                .parallelStream()
                .filter(this::tryReserveSlaveAvailability)
                .collect(Collectors.toList());
        super.scheduler.schedule(availableSlaves, request);
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
        final double currentSlaveAvailability  = slave.getAvailability().get();
        final double slaveAvailabilityAfterCompute = currentSlaveAvailability - slave.getAvailabilityReducePerCompute();
        if(slaveAvailabilityAfterCompute >= 0d) {

            reserved = slave.getAvailability().compareAndSet(currentSlaveAvailability, slaveAvailabilityAfterCompute);

        }
        return reserved;
    }
}
