package datastructures.handler;

import datastructures.*;
import datastructures.list.FixedSizeLockFreeList;
import datastructures.list.LockFreeList;
import datastructures.map.LockFreeMap;
import datastructures.scheduler.SlaveScheduler;
import master.Master;
import slave.Slave;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LockFreeSlaveHandler extends SlaveHandler {

    private final FixedSizeLockFreeList<Slave> slaves;

    private final LockFreeList<Request> priorityRequestQueue;

    private final LockFreeMap<Integer, FixedSizeLockFreeList<Result>> computationResults = new LockFreeMap<>();

    private final ScheduledExecutorService rescheduleExecutor;

    public LockFreeSlaveHandler(final SlaveScheduler scheduler, final Master master, final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves = new FixedSizeLockFreeList<>(slaves.size());
        this.priorityRequestQueue = new LockFreeList<>();
        this.slaves.addAll(slaves);
        this.rescheduleExecutor = Executors.newScheduledThreadPool(slaves.size());
    }

    @Override
    public void requestSlaves(final Request request) {

        Request requestForSlavesToProcess;

        if(this.priorityRequestQueue.isEmpty()) {
            requestForSlavesToProcess = request;
        } else {
            final Request headRequest = this.priorityRequestQueue.poll();

            if(headRequest == null) {
                requestForSlavesToProcess = request;
            } else {
                final List<Request> parcelsOfRequest = this.priorityRequestQueue.parallelStream().filter(request1 -> request1.getRequestID() == headRequest.getRequestID()).collect(Collectors.toCollection(ArrayList::new));
                final List<Request> requestsToFold = new ArrayList<>();

                // concurrent queue pops might occur so the requests to fold might be outdated
                // to solve this we only fold the requests that were removed on this stage
                for(Request requestToRemoveFromPriorityQueue : parcelsOfRequest) {
                    if(this.priorityRequestQueue.remove(requestToRemoveFromPriorityQueue)) {
                        requestsToFold.add(requestToRemoveFromPriorityQueue);
                    }
                }
                requestForSlavesToProcess = foldRequests(headRequest, requestsToFold);
            }
        }

        List<SlaveToSchedule> slavesToSchedule = slaves
                .parallelStream()
                .map(slave -> new SlaveToSchedule(slave, tryReserveSlaveAvailability(slave, requestForSlavesToProcess)))
                .collect(Collectors.toList());

        this.scheduler.schedule(slavesToSchedule, requestForSlavesToProcess, this);
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
    public void reportCouldNotProcessRequest(Slave slave, Request request) {
        if(request instanceof ReportPerformanceIndexRequest) {
            this.rescheduleRequestToSlaveInTheFuture(slave, request);
        } else {
            final Optional<Slave> optionalSlave =  this.slaves.parallelStream().filter(slave1 -> slave1 != slave).findAny();

            if(optionalSlave.isPresent()) {
                optionalSlave.get().process(request, this);
            } else {
                this.rescheduleRequestToSlaveInTheFuture(slave, request);
            }
        }
    }

    private void rescheduleRequestToSlaveInTheFuture(final Slave slave, final Request request) {
        this.rescheduleExecutor.schedule(() -> slave.process(request, this), 5, TimeUnit.SECONDS);
    }

    private Request foldRequests(final Request startRequest, final List<Request> requests) {

        if(startRequest instanceof CodeExecutionRequest) {
            return requests
                    .parallelStream()
                    .map(CodeExecutionRequest.class::cast)
                    .reduce(new CodeExecutionRequest(
                                new ArrayList<>(), startRequest.getRequestID(), ((CodeExecutionRequest) startRequest).getOp()),
                            (request, request2) -> {
                        request.getNumbers().addAll(request2.getNumbers());
                        return request;
                    });
        } else {
            return startRequest;
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

    @Override
    public String toString() {
        return "LockFreeSlaveHandler{" +
                "slaves=" + slaves +
                ", computationResults=" + computationResults +
                '}';
    }
}
