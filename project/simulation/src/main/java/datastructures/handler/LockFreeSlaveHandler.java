package datastructures.handler;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import datastructures.*;
import datastructures.list.FixedSizeLockFreeList;
import datastructures.map.LockFreeMap;
import datastructures.scheduler.SlaveScheduler;
import master.Master;
import slave.Slave;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LockFreeSlaveHandler extends SlaveHandler {

    private final FixedSizeLockFreeList<Slave> slaves;

    protected final LockFreeMap<Long, FixedSizeLockFreeList<Result>> computationResults = new LockFreeMap<>();

    private final ScheduledExecutorService rescheduleExecutor;

    public LockFreeSlaveHandler(final SlaveScheduler scheduler, final Master master, final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves = new FixedSizeLockFreeList<>(slaves.size());
        this.slaves.addAll(slaves);
        this.rescheduleExecutor = Executors.newScheduledThreadPool(slaves.size());
    }

    @Override
    public void requestSlaves(final Request request) {

        List<SlaveToSchedule> slavesToSchedule = slaves
                .stream()
                .map(slave -> new SlaveToSchedule(slave, tryReserveSlaveAvailability(slave, request)))
                .collect(Collectors.toList());

        this.scheduler.schedule(slavesToSchedule, request, this);
    }

    @Override
    public void pushResult(final Result result) {
        final FixedSizeLockFreeList<Result> results = computationResults.get(result.getRequestID());
        results.add(result);
        if (results.hasReachedMaxSize()) {
            final int finalResultSum;
            Result finalResult;
            switch (result.getOperation()) {
                case ADD:
                    finalResultSum = results.parallelStream().mapToInt(Result::getValue).sum();
                    finalResult = new Result(finalResultSum, result.getRequestID(), result.getOperation());
                    break;
                case MULTIPLY:
                    finalResultSum = results.parallelStream().mapToInt(Result::getValue).reduce(1, (i, i1) -> i * i1);
                    finalResult = new Result(finalResultSum, result.getRequestID(), result.getOperation());
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

        if (tryUnreserveSlaveAvailability(slave, request)) {
            final AvailabilityDetails details = new AvailabilityDetails(slave, slave.getAvailability().intValue());

            super.master.receiveSlaveAvailability(details);
        } else {
            reportAvailability(slave, request);
        }

    }

    @Override
    public void reportCouldNotProcessRequest(Slave slave, Request request) {
        if (request instanceof ReportPerformanceIndexRequest) {
            this.rescheduleRequestToSlaveInTheFuture(slave, request);
        } else {
            final Optional<Slave> optionalSlave = this.slaves.stream().filter(slave1 -> slave1 != slave).findAny();

            if (optionalSlave.isPresent()) {

                final Slave chosenSlave = optionalSlave.get();

                if (tryReserveSlaveAvailability(chosenSlave, request)) {

                    chosenSlave.process(request, this);

                } else {
                    this.rescheduleRequestToSlaveInTheFuture(slave, request);
                }
            } else {
                this.rescheduleRequestToSlaveInTheFuture(slave, request);
            }
        }
    }

    @Override
    public void notifyScheduledRequests(Request request, int numberOfSchedules) {
        if (request instanceof CodeExecutionRequest) {
            this.computationResults.put(request.getRequestID(), new FixedSizeLockFreeList<>(numberOfSchedules));
        }
    }

    @Override
    public Slave removeSlave(Slave slave) {
        this.slaves.remove(slave);

        slave.shutdown();

        return slave;
    }

    @Override
    public void addSlave(Slave slave) {
        this.slaves.add(slave);
    }

    @Override
    public List<Slave> availableSlaves() {
        return new ArrayList<>(this.slaves);
    }

    protected void rescheduleRequestToSlaveInTheFuture(final Slave slave, final Request request) {
        this.rescheduleExecutor.schedule(() -> {
            slave.process(request, this);
        }, 10, TimeUnit.SECONDS);

    }

    private boolean tryReserveSlaveAvailability(final Slave slave, final Request request) {
        boolean reserved = false;
        final int currentSlaveAvailability = slave.getAvailability().get();
        final int slaveAvailabilityAfterCompute = currentSlaveAvailability - slave.getAvailabilityReducePerCompute(request);
        if (slaveAvailabilityAfterCompute >= 0) {

            reserved = slave.getAvailability().compareAndSet(currentSlaveAvailability, slaveAvailabilityAfterCompute);

        }
        return reserved;
    }

    private boolean tryUnreserveSlaveAvailability(final Slave slave, final Request request) {
        boolean unreserved = true;
        final int currentSlaveAvailability = slave.getAvailability().get();
        final int slaveAvailabilityAfterCompute = currentSlaveAvailability + slave.getAvailabilityReducePerCompute(request);
        if (slaveAvailabilityAfterCompute <= 100) {

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
