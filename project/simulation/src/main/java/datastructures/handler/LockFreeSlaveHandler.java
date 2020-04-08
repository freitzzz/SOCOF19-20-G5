package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import main.java.datastructures.lists.LockFreeList;
import main.java.datastructures.lists.LockFreeMap;
import slave.Slave;

public class LockFreeSlaveHandler implements datastructures.handler.SlaveHandler {

    private final LockFreeList<Slave> slaves = new LockFreeList<>();

    private final LockFreeMap<Integer, LockFreeList<Result>> computationResults = new LockFreeMap<>();

    @Override
    public void requestComputation(Request request) {
        /**
         * Happy path
         * 1 - initialize required local variables (requests map = <Slave, Request>[], start index = 0
         * 2 - for all slaves
         *  2.1 - Try to reserve a piece of availability of the slave
         *      2.1.1 - If so add slave to requests map
         * 3 - for all slaves in request map
         *  3.1 - get slave performance index
         *  3.2 - get request sequence length
         *  3.3 - apply load balance formula by the given performance index, sequence length and number of slaves
         *  available to compute a request - this returns an integer that indicates the length of the sequence
         *  of numbers to apply the request
         *  3.4 - slice the sequence of numbers from start index to formula result
         *  3.5 - create new request with created slice index and add it to requests list
         * 4 - for all requests in map tell slave to execute request
         *
         * Uncaught situation: What happens if no slaves are available ?
         */
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
