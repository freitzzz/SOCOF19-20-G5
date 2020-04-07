package datastructures.scheduler;

import datastructures.Request;
import slave.Slave;

import java.util.List;

public final class PerformanceIndexSlaveScheduler implements SlaveScheduler {

    @Override
    public void schedule(List<Slave> slaves, Request request) {

        int startIndex = 0;

        for (Slave slave : slaves) {
            final int formulaResult = request.numbers.size() * ( (slave.getPerformanceIndex() * slaves.size() ) / 100);

            List<Integer> numbersSlice = request.numbers.subList(startIndex, formulaResult);

            startIndex = formulaResult;

            slave.compute(new Request(numbersSlice, request.requestID, request.op));
        }
    }
}
