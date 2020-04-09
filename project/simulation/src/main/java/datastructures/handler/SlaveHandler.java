package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import datastructures.scheduler.SlaveScheduler;
import master.Master;
import slave.Slave;

public abstract class SlaveHandler {

    protected final SlaveScheduler scheduler;

    protected final Master master;

    public SlaveHandler(final SlaveScheduler scheduler, final Master master) {

        this.scheduler = scheduler;

        this.master = master;

    }

    public abstract void requestComputation(Request request);
    public abstract void pushResult(Result result);
    public abstract void reportPerformance(Slave slave, int index);
    public abstract void reportAvailability(Slave slave, double availability);
}
