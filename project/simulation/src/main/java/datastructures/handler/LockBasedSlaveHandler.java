package datastructures.handler;

import datastructures.*;
import datastructures.list.FixedSizeLockFreeList;
import datastructures.list.LockBasedList;
import datastructures.map.LockBasedMap;
import master.Master;
import slave.Slave;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import datastructures.scheduler.SlaveScheduler;

public class LockBasedSlaveHandler extends SlaveHandler{

    ReentrantLock lock = new ReentrantLock();

    private LockBasedList<Slave> slaves;

    private LockBasedMap<Integer, LockBasedList<Result>> computationResults = new LockBasedMap<>();

    private final ScheduledExecutorService rescheduleExecutor;


    public LockBasedSlaveHandler(final SlaveScheduler scheduler, final Master master,  final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves = new LockBasedList<>(slaves.size());
        this.slaves.addAll(slaves);
        this.rescheduleExecutor = Executors.newScheduledThreadPool(slaves.size());
    }


    @Override
    public void requestSlaves(Request request) {
        int slaveAvailabilityAfterCompute = 0;
        int currentSlaveAvailability = 0;
        List<SlaveToSchedule> slavesToSchedule = new ArrayList<>();
        System.out.println("In requestSlaves, thread " + Thread.currentThread().getId()+ " waiting to get lock");
        lock.lock();
        try{
            System.out.println("Count of locks held by thread " + Thread.currentThread().getId() + " - " + lock.getHoldCount());
            for(Slave slave : slaves){
                currentSlaveAvailability  = slave.getAvailability().get();
                slaveAvailabilityAfterCompute = currentSlaveAvailability - slave.getAvailabilityReducePerCompute(request);
                if(slaveAvailabilityAfterCompute >= 0) {
                    SlaveToSchedule slave1 = new SlaveToSchedule(slave, true);
                    slavesToSchedule.add(slave1);
                    slave.getAvailability().set(slaveAvailabilityAfterCompute);
                }
            }
            this.scheduler.schedule(slavesToSchedule, request, this);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void pushResult(Result result) {
            System.out.println("In pushResult, thread " + Thread.currentThread().getId() + " waiting to get lock");
            lock.lock();
            try {
                System.out.println("Count of locks held by thread " + Thread.currentThread().getId() + " - " + lock.getHoldCount());

                final LockBasedList<Result> results = computationResults.get(result.getRequestID());
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
            } finally {
                lock.unlock();
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
        System.out.println("In reportAvailability, thread " + Thread.currentThread().getId()+ " waiting to get lock");
        lock.lock();
        try {
            System.out.println("Count of locks held by thread " + Thread.currentThread().getId() + " - " + lock.getHoldCount());
            currentSlaveAvailability  = slave.getAvailability().get();
            slaveAvailabilityAfterCompute = currentSlaveAvailability + slave.getAvailabilityReducePerCompute(request);
            if(slaveAvailabilityAfterCompute <= 100) {
                final AvailabilityDetails details = new AvailabilityDetails(slave, slave.getAvailability().intValue());
                slave.getAvailability().set(slaveAvailabilityAfterCompute);
                super.master.receiveSlaveAvailability(details);
            } else {
                reportAvailability(slave, request);
            }

        } finally {
            lock.unlock();
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

    @Override
    public void notifyScheduledRequests(Request request, int numberOfSchedules) {
        if(request instanceof CodeExecutionRequest) {
            this.computationResults.put(request.getRequestID(), new LockBasedList<>(numberOfSchedules));
        }
    }


    protected void rescheduleRequestToSlaveInTheFuture(final Slave slave, final Request request) {
        this.rescheduleExecutor.schedule(() -> slave.process(request, this), 5, TimeUnit.SECONDS);
    }



    @Override
    public String toString() {
        return "LockBasedSlaveHandler{" +
                "lock=" + lock +
                ", slaves=" + slaves +
                ", computationResults=" + computationResults +
                '}';
    }
}