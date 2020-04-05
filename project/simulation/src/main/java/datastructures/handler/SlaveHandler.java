package main.java.datastructures.handler;

import main.java.datastructures.Request;
import main.java.datastructures.Result;
import main.java.slave.Slave;

public interface SlaveHandler {

    void requestComputation(Request request);
    void pushResult(Result result);
    void reportPerformance(Slave slave, int index);
    void reportAvailability(Slave slave, double availability);
}
