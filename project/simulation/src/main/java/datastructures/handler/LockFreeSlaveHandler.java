package datastructures.handler;

import datastructures.*;
import datastructures.list.FixedSizeLockFreeList;
import datastructures.list.LockFreeList;
import datastructures.map.LockFreeMap;
import datastructures.scheduler.SlaveScheduler;
import master.Master;
import slave.Slave;

import java.util.List;
import java.util.stream.Collectors;

public class LockFreeSlaveHandler extends SlaveHandler {

    private final FixedSizeLockFreeList<Slave> slaves;

    private final LockFreeList<Request> priorityRequestQueue;

    private final LockFreeMap<Integer, FixedSizeLockFreeList<Result>> computationResults = new LockFreeMap<>();

    public LockFreeSlaveHandler(final SlaveScheduler scheduler, final Master master, final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves = new FixedSizeLockFreeList<>(slaves.size());
        this.priorityRequestQueue = new LockFreeList<>();
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
                computationResults.put(request.getRequestID(), new FixedSizeLockFreeList<>(availableSlaves.size()));
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
        final FixedSizeLockFreeList<Result> results = computationResults.get(result.getRequestID());
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

    @Override
    public void reportCouldNotProcessRequest(Request request) {

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

    @Override
    public String toString() {
        return "LockFreeSlaveHandler{" +
                "slaves=" + slaves +
                ", computationResults=" + computationResults +
                '}';
    }
}
