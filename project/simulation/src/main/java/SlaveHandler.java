package main.java;

public interface SlaveHandler {

    void requestComputation(Request request);
    void pushResult(Result result);
    void reportPerformance(Slave slave, int index);
    void reportAvailability(Slave slave, double availability);
}
