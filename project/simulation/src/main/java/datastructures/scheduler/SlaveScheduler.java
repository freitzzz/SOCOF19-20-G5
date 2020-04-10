package datastructures.scheduler;

import java.util.List;

import datastructures.CodeExecutionRequest;

import datastructures.handler.SlaveHandler;
import slave.Slave;

public interface SlaveScheduler {

    void schedule(final List<Slave> slaves, final CodeExecutionRequest request, final SlaveHandler slaveHandler);

}
