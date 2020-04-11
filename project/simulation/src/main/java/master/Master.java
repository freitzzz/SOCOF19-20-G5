package master;

import datastructures.*;
import datastructures.handler.LockBasedSlaveHandler;
import datastructures.handler.LockFreeSlaveHandler;
import datastructures.handler.SlaveHandler;
import datastructures.scheduler.PerformanceIndexSlaveScheduler;
import datastructures.scheduler.SlaveScheduler;
import slave.Slave;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master {

    private final ExecutorService executor;

    private final SlaveHandler slaveHandler;

    private volatile int requestPerformed = 0;

    private Master(final List<Slave> slavesToConnect, final SlaveScheduler scheduler, final SlaveHandler.Type slaveHandlerType, final int numberOfWorkers) {

        List<Slave> connectedSlaves = new ArrayList<>(slavesToConnect);

        this.executor = Executors.newFixedThreadPool(numberOfWorkers);

        if(slaveHandlerType == SlaveHandler.Type.LOCK_FREE) {
            this.slaveHandler = new LockFreeSlaveHandler(scheduler, this, connectedSlaves);
        } else {
            this.slaveHandler = new LockBasedSlaveHandler(scheduler, this, connectedSlaves);
        }

    }

    public void receiveResult(final Result result) {
        System.out.printf("Received result of code execution request #%d:\n\t- Result: %d\n", result.getRequestID(), result.getValue());

    }

    public void receiveRequestCouldNotBeScheduled(final Request request) {

        System.out.printf("Request #%d could not be scheduled, scheduling again in %d milliseconds\n", request.getRequestID(), 5000);

        executor.submit(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(request instanceof ReportPerformanceIndexRequest) {
                requestSlavesPerformanceIndex();
            } else {
                final CodeExecutionRequest requestAsCEQ = (CodeExecutionRequest) request;

                if(requestAsCEQ.getOp() == CodeExecutionRequest.Operation.ADD) {
                    requestSumOfNumbers(requestAsCEQ.getNumbers());
                } else {
                    requestMultiplicationOfNumbers(requestAsCEQ.getNumbers());
                }
            }
        });

    }

    public void receiveSlavePerformanceDetails(final PerformanceDetails details) {

        System.out.printf("Received performance details of Slave %s :\n\t - Performance Index: %d\n", details.slave, details.performanceIndex);

    }

    public void receiveSlaveAvailability(final AvailabilityDetails details) {

        System.out.printf("Slave %s just reported his availability details:\n\t- Availability: %d%%\n", details.slave, details.availability);

    }

    public void requestSumOfNumbers(final List<Integer> numbers) {

        executor.submit(() -> {
            slaveHandler.requestSlaves(new CodeExecutionRequest(numbers, requestPerformed++, CodeExecutionRequest.Operation.ADD));
        });

    }

    public void requestMultiplicationOfNumbers(final List<Integer> numbers) {

        executor.submit(() -> {
            slaveHandler.requestSlaves(new CodeExecutionRequest(numbers, requestPerformed++, CodeExecutionRequest.Operation.ADD));
        });

    }

    public void requestSlavesPerformanceIndex() {

        executor.submit(() -> {
            slaveHandler.requestSlaves(new ReportPerformanceIndexRequest(requestPerformed++));
        });

    }

    public static class MasterBuilder {

        private List<Slave> slaves = new ArrayList<>();

        private SlaveHandler.Type slaveHandlerType;

        private SlaveScheduler scheduler;

        private int workers = 0;

        private MasterBuilder(){}

        public static MasterBuilder create() {
            return new MasterBuilder();
        }

        public MasterBuilder withSlave(final int performanceIndex) {
            slaves.add(new Slave(performanceIndex));
            return this;
        }

        public MasterBuilder withPerformanceIndexScheduler() {
            scheduler = new PerformanceIndexSlaveScheduler();
            return this;
        }

        public MasterBuilder withLockBasedSlaveHandler() {
            slaveHandlerType = SlaveHandler.Type.LOCK_BASED;
            return this;
        }

        public MasterBuilder withLockFreeSlaveHandler() {
            slaveHandlerType = SlaveHandler.Type.LOCK_FREE;
            return this;
        }

        public MasterBuilder withWorker(){
            workers++;
            return this;
        }

        public Master build() {
            return new Master(slaves, scheduler, slaveHandlerType, workers);
        }

        @Override
        public String toString() {
            return "MasterBuilder{" +
                    "slaves=" + slaves +
                    ", slaveHandlerType=" + slaveHandlerType +
                    ", scheduler=" + scheduler +
                    ", workers=" + workers +
                    '}';
        }
    }
}
