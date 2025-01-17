package slave;

import datastructures.CodeExecutionRequest;
import datastructures.ReportPerformanceIndexRequest;
import datastructures.Request;
import datastructures.handler.SlaveHandler;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Slave implements Comparable<Slave> {

    //declare variables
    private int performanceIndex;
    private AtomicInteger availability;
    private final ExecutorService exec;
    private final Random randomFailureMachine;

    //implement setters and getters
    public int getPerformanceIndex() {
        return performanceIndex;
    }

    public AtomicInteger getAvailability() {
        return availability;
    }

    //add constructor
    public Slave(int performanceIndex) {
        this.performanceIndex = performanceIndex;
        this.availability = new AtomicInteger(100);
        this.exec = Executors.newFixedThreadPool(performanceIndex);
        this.randomFailureMachine = new Random();
    }

    public void process(Request request, SlaveHandler slaveHandler) {

        final boolean isCodeExecutionRequest = request instanceof CodeExecutionRequest;

        if(!exec.isShutdown()) {

            if (tryToRandomlyFail()) {
                slaveHandler.reportCouldNotProcessRequest(this, request);
                slaveHandler.reportAvailability(this, request);
            } else {


                Runnable taskToBeExecuted;

                if (isCodeExecutionRequest) {
                    taskToBeExecuted = new ComputeThread((CodeExecutionRequest) request, slaveHandler, this);
                } else {
                    taskToBeExecuted = new ReportPerformanceIndexThread((ReportPerformanceIndexRequest) request, slaveHandler, this);
                }
                exec.execute(taskToBeExecuted);
            }
        } else {
            if(isCodeExecutionRequest) {
                slaveHandler.reportCouldNotProcessRequest(this, request);
            }
        }
    }

    public int getAvailabilityReducePerCompute(Request request) {

        if (request instanceof CodeExecutionRequest) {
            return 25; // this may vary
        } else {
            return 0;
        }
    }

    public void shutdown() {
        exec.shutdownNow();
    }

    // This method needs to be spied on tests, otherwise it will fail tests randomly

    protected boolean tryToRandomlyFail() {
        return this.randomFailureMachine.nextInt(10) > 8;
    }

    @Override
    public int compareTo(Slave slave) {
        return Integer.compare(this.getPerformanceIndex(), slave.getPerformanceIndex());
    }
}
