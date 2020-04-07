package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import slave.Slave;

public interface SlaveHandler {

    void requestComputation(Request request);
    void pushResult(Result result);
    void reportPerformance(Slave slave, int index);
    void reportAvailability(Slave slave, double availability);
}
