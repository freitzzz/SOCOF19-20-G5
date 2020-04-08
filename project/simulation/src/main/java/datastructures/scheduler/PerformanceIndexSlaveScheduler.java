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
        double overallPerformance = 0;
        double numbersSize= request.getNumbers().size();

        for (Slave slave : slaves) {
            overallPerformance+=slave.getPerformanceIndex();
        }

        for (Slave slave : slaves) {
            int pi = slave.getPerformanceIndex();
            int formulaResult = (int) Math.round(numbersSize * (pi / overallPerformance));
            if(formulaResult < 2){
                overallPerformance-=pi;
                continue;
            }
            if(startIndex+formulaResult == numbersSize-1){
                formulaResult++;
            }

            List<Integer> numbersSlice = request.getNumbers().subList(startIndex, startIndex+formulaResult);

            slave.compute(new Request(numbersSlice, request.getRequestID(), request.getOp()));
            if(startIndex+formulaResult == numbersSize){
                break;
            }else{
                startIndex = startIndex+formulaResult;
            }
        }
    }
}
