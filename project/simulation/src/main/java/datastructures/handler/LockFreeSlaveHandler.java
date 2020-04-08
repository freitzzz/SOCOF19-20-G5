package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import datastructures.list.LockFreeList;
import datastructures.map.LockFreeMap;
import datastructures.scheduler.SlaveScheduler;
import slave.Slave;

public class LockFreeSlaveHandler extends SlaveHandler {

    private final LockFreeList<Slave> slaves = new LockFreeList<>();

    private final LockFreeMap<Integer, LockFreeList<Result>> computationResults = new LockFreeMap<>();

    public LockFreeSlaveHandler(SlaveScheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void requestComputation(Request request) {



    }

    @Override
    public void pushResult(Result result) {

    }

    @Override
    public void reportPerformance(Slave slave, int index) {

    }

    @Override
    public void reportAvailability(Slave slave, double availability) {

    }
}
