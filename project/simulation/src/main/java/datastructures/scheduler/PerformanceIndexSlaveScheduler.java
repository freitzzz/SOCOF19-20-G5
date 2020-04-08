package datastructures.scheduler;

import datastructures.Request;
import slave.Slave;

import java.util.List;

public final class PerformanceIndexSlaveScheduler implements SlaveScheduler {

    /*
    1, 2, ,3 ,  4, 5 - PI: €PI = 15
    Formula: NS * (PI / €PI)
    Example:
        NS: 5
        PI: 5
        SS: 5
        FR = 5 * (5 * 5) / 100
        FR =
     */

    @Override
    public void schedule(List<Slave> slaves, Request request) {

        int startIndex = 0;

        for (Slave slave : slaves) {
            final int formulaResult = request.getNumbers().size() * ( (slave.getPerformanceIndex() * slaves.size() ) / 100);

            List<Integer> numbersSlice = request.getNumbers().subList(startIndex, formulaResult);

            startIndex = formulaResult;

            slave.compute(new Request(numbersSlice, request.getRequestID(), request.getOp()));
        }
    }
}
