package datastructures.scheduler;

import java.util.List;

import datastructures.Request;
import datastructures.SlaveToSchedule;
import datastructures.handler.SlaveHandler;

public interface SlaveScheduler {

    void schedule(final List<SlaveToSchedule> slaves, final Request request, final SlaveHandler slaveHandler);

}
