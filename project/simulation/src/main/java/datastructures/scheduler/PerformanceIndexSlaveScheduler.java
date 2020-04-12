package datastructures.scheduler;

import datastructures.CodeExecutionRequest;
import datastructures.handler.SlaveHandler;
import slave.Slave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PerformanceIndexSlaveScheduler implements SlaveScheduler {

    @Override
    public void schedule(final List<Slave> slaves, final CodeExecutionRequest request, final SlaveHandler slaveHandler) {

        int startIndex = 0;
        double overallPerformance = 0;
        int numbersSize= request.getNumbers().size();
        List<Slave> unrequestedSlaves = new ArrayList<>();

        Collections.sort(slaves);

        for (Slave slave : slaves) {
            overallPerformance+=slave.getPerformanceIndex();
        }
        int i =0;
        for (i = 0; i < slaves.size(); i++) {

            if(i == slaves.size()-1){
                List<Integer> numbersSlice = request.getNumbers().subList(startIndex, numbersSize);

                slaves.get(i).process(new CodeExecutionRequest(numbersSlice, request.getRequestID(), request.getOp()), slaveHandler);
                i++;
                break;
            }

            int pi = slaves.get(i).getPerformanceIndex();
            int formulaResult = (int) (numbersSize * (pi / overallPerformance));

            if(formulaResult < 2){
                overallPerformance-=pi;
                unrequestedSlaves.add(slaves.get(i));
                continue;
            }

            int endIndex=startIndex+formulaResult;

            if(numbersSize-endIndex < 2){
                endIndex=numbersSize;
            }

            List<Integer> numbersSlice = request.getNumbers().subList(startIndex, endIndex);

            slaves.get(i).process(new CodeExecutionRequest(numbersSlice, request.getRequestID(), request.getOp()), slaveHandler);

            if(endIndex == numbersSize){
                i++;
                break;
            }else{
                startIndex = endIndex;
            }
        }
        for(; i< slaves.size();i++){
            unrequestedSlaves.add(slaves.get(i));
        }
        for(Slave s : unrequestedSlaves){
            s.process(new CodeExecutionRequest(Collections.emptyList(),request.getRequestID(),request.getOp()),slaveHandler);
        }
    }
}
