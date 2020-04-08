package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import datastructures.scheduler.SlaveScheduler;
import slave.Slave;

public abstract class SlaveHandler {

    protected final SlaveScheduler scheduler;

    public SlaveHandler(SlaveScheduler scheduler) {

        this.scheduler = scheduler;

    }

    public abstract void requestComputation(Request request);
    public abstract void pushResult(Result result);
    public abstract void reportPerformance(Slave slave, int index);
    public abstract void reportAvailability(Slave slave, double availability);
}
