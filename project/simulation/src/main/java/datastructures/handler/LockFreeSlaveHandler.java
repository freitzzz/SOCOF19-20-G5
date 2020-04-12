package datastructures.handler;

import datastructures.*;
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
    public void requestSlaves(final Request request) {
        List<Slave> availableSlaves = slaves
                .parallelStream()
                .filter(slave -> tryReserveSlaveAvailability(slave, request))
                .collect(Collectors.toList());

        if(availableSlaves.size() > 0) {

            if(request instanceof CodeExecutionRequest) {
                computationResults.put(request.getRequestID(), new LockFreeList<>(availableSlaves.size()));
                super.scheduler.schedule(availableSlaves, (CodeExecutionRequest) request, this);
            } else {
                slaves.forEach(slave -> slave.process(request, this));
            }

        } else {
            super.master.receiveRequestCouldNotBeScheduled(request);
        }
    }

    @Override
    public void pushResult(final Result result) {
        final LockFreeList<Result> results = computationResults.get(result.getRequestID());
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

        if(tryUnreserveSlaveAvailability(slave, request)) {
            final AvailabilityDetails details = new AvailabilityDetails(slave, slave.getAvailability().intValue());

            super.master.receiveSlaveAvailability(details);
        } else {
            reportAvailability(slave, request);
        }

    }

    private boolean tryReserveSlaveAvailability(final Slave slave, final Request request) {
        boolean reserved = false;
        final int currentSlaveAvailability  = slave.getAvailability().get();
        final int slaveAvailabilityAfterCompute = currentSlaveAvailability - slave.getAvailabilityReducePerCompute(request);
        if(slaveAvailabilityAfterCompute >= 0) {

            reserved = slave.getAvailability().compareAndSet(currentSlaveAvailability, slaveAvailabilityAfterCompute);

        }
        return reserved;
    }

    private boolean tryUnreserveSlaveAvailability(final Slave slave, final Request request) {
        boolean unreserved = false;
        final int currentSlaveAvailability  = slave.getAvailability().get();
        final int slaveAvailabilityAfterCompute = currentSlaveAvailability + slave.getAvailabilityReducePerCompute(request);
        if(slaveAvailabilityAfterCompute <= 100) {

            unreserved = slave.getAvailability().compareAndSet(currentSlaveAvailability, slaveAvailabilityAfterCompute);

        }
        return unreserved;
    }
}
