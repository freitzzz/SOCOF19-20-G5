package datastructures.scheduler;

import java.util.List;

import datastructures.Request;

import slave.Slave;

public interface SlaveScheduler {

    void schedule(List<Slave> slaves, Request request);

}
