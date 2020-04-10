package datastructures.scheduler;

import datastructures.Request;
import datastructures.handler.SlaveHandler;
import slave.Slave;

import java.util.Collections;
import java.util.List;

public final class PerformanceIndexSlaveScheduler implements SlaveScheduler {

    @Override
    public void schedule(List<Slave> slaves, Request request, SlaveHandler slaveHandler) {

        int startIndex = 0;
        double overallPerformance = 0;
        int numbersSize= request.getNumbers().size();

        Collections.sort(slaves);

        for (Slave slave : slaves) {
            overallPerformance+=slave.getPerformanceIndex();
        }

        for (int i = 0; i < slaves.size(); i++) {

            if(i == slaves.size()-1){
                List<Integer> numbersSlice = request.getNumbers().subList(startIndex, numbersSize);

                slaves.get(i).compute(new Request(numbersSlice, request.getRequestID(), request.getOp()), slaveHandler);
                break;
            }

            int pi = slaves.get(i).getPerformanceIndex();
            int formulaResult = (int) (numbersSize * (pi / overallPerformance));

            if(formulaResult < 2){
                overallPerformance-=pi;
                continue;
            }

            int endIndex=startIndex+formulaResult;

            if(numbersSize-endIndex < 2){
                endIndex=numbersSize;
            }

            List<Integer> numbersSlice = request.getNumbers().subList(startIndex, endIndex);

            slaves.get(i).compute(new Request(numbersSlice, request.getRequestID(), request.getOp()), slaveHandler);

            if(endIndex == numbersSize){
                break;
            }else{
                startIndex = endIndex;
            }
        }
    }
}
