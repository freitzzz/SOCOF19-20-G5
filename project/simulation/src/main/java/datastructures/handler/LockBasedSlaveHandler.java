package datastructures.handler;

import datastructures.*;
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

    protected final LockBasedMap<Integer, LockBasedList<Result>> computationResults = new LockBasedMap<>();

    private final ScheduledExecutorService rescheduleExecutor;


    public LockBasedSlaveHandler(final SlaveScheduler scheduler, final Master master,  final List<Slave> slaves) {
        super(scheduler, master);
        this.slaves = new LockBasedList<>(slaves.size());
        this.slaves.addAll(slaves);
        this.rescheduleExecutor = Executors.newScheduledThreadPool(slaves.size());
    }


    @Override
    public void requestSlaves(Request request) {
        List<SlaveToSchedule> slavesToSchedule = new ArrayList<>();
        lock.lock();
        try{
            for(Slave slave : slaves){

                final boolean reserved = tryReserveSlaveAvailability(slave, request);

                SlaveToSchedule slave1 = new SlaveToSchedule(slave, reserved);

                slavesToSchedule.add(slave1);
            }

            this.scheduler.schedule(slavesToSchedule, request, this);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void pushResult(final Result result) {
        lock.lock();
        try {
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
        lock.lock();
        try {
            currentSlaveAvailability  = slave.getAvailability().get();
            slaveAvailabilityAfterCompute = currentSlaveAvailability + slave.getAvailabilityReducePerCompute(request);
            if(slaveAvailabilityAfterCompute <= 100) {
                final AvailabilityDetails details = new AvailabilityDetails(slave, slave.getAvailability().intValue());
                slave.getAvailability().set(slaveAvailabilityAfterCompute);
                super.master.receiveSlaveAvailability(details);
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
            lock.lock();

            try {

                final Optional<Slave> optionalSlave = this.slaves.parallelStream().filter(slave1 -> slave1 != slave).findAny();

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

            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void notifyScheduledRequests(Request request, int numberOfSchedules) {
        if(request instanceof CodeExecutionRequest) {
            this.computationResults.put(request.getRequestID(), new LockBasedList<>(numberOfSchedules));
        }
    }

    @Override
    public Slave removeSlave(Slave slave) {
<<<<<<< HEAD
        return null;
=======
        this.slaves.remove(slave);

        slave.shutdown();

        return slave;
>>>>>>> 30cffc3249a8b5e2cf912ca9f3d64ceb2788c22e
    }

    @Override
    public void addSlave(Slave slave) {
<<<<<<< HEAD

=======
        this.slaves.add(slave);
>>>>>>> 30cffc3249a8b5e2cf912ca9f3d64ceb2788c22e
    }

    @Override
    public List<Slave> availableSlaves() {
<<<<<<< HEAD
        return null;
=======
        return new ArrayList<>(this.slaves);
>>>>>>> 30cffc3249a8b5e2cf912ca9f3d64ceb2788c22e
    }


    protected void rescheduleRequestToSlaveInTheFuture(final Slave slave, final Request request) {
        this.rescheduleExecutor.schedule(() -> slave.process(request, this), 300, TimeUnit.MILLISECONDS);
    }

    private boolean tryReserveSlaveAvailability(final Slave slave, final Request request) {
        final int currentSlaveAvailability  = slave.getAvailability().get();
        final int slaveAvailabilityAfterCompute = currentSlaveAvailability - slave.getAvailabilityReducePerCompute(request);
        if(slaveAvailabilityAfterCompute >= 0) {
            slave.getAvailability().set(slaveAvailabilityAfterCompute);
            return true;
        }
        else {
            return false;
        }
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