package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import datastructures.scheduler.SlaveScheduler;
import master.Master;
import slave.Slave;

import java.util.List;

public abstract class SlaveHandler {

    protected final SlaveScheduler scheduler;

    protected final Master master;

    public SlaveHandler(final SlaveScheduler scheduler, final Master master) {

        this.scheduler = scheduler;

        this.master = master;

    }

    public abstract void requestSlaves(Request request);
    public abstract void pushResult(Result result);
    public abstract void reportPerformance(Slave slave);
    public abstract void reportAvailability(Slave slave, Request request);
    public abstract void reportCouldNotProcessRequest(Slave slave, Request request);
    public abstract void notifyScheduledRequests(Request request, int numberOfSchedules);
    public abstract Slave removeSlave(Slave slave);
    public abstract void addSlave(Slave slave);

    public abstract List<Slave> availableSlaves();

    public enum Type {
        LOCK_BASED,
        LOCK_FREE
    }
}
