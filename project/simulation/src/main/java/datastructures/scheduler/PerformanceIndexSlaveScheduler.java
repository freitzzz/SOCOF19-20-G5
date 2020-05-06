package datastructures.scheduler;

import datastructures.CodeExecutionRequest;
import datastructures.ReportPerformanceIndexRequest;
import datastructures.Request;
import datastructures.SlaveToSchedule;
import datastructures.handler.SlaveHandler;
import slave.Slave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class PerformanceIndexSlaveScheduler implements SlaveScheduler {

    private void scheduleComputation(List<SlaveToSchedule> slaves, final CodeExecutionRequest request, final SlaveHandler slaveHandler) {
        int startIndex = 0;
        double overallPerformance = 0;
        int numbersSize= request.getNumbers().size();
        List<SlaveToSchedule> unrequestedSlaves = new ArrayList<>();

        Collections.sort(slaves);

        for (SlaveToSchedule slave : slaves) {
            overallPerformance+=slave.slave.getPerformanceIndex();
        }
        int i =0;
        for (i = 0; i < slaves.size(); i++) {

            if(i == slaves.size()-1){
                List<Integer> numbersSlice = request.getNumbers().subList(startIndex, numbersSize);

                tryToProcess(slaves.get(i),new CodeExecutionRequest(numbersSlice, request.getRequestID(), request.getOp()),slaveHandler);
                //slaves.get(i).process(new CodeExecutionRequest(numbersSlice, request.getRequestID(), request.getOp()), slaveHandler);
                i++;
                break;
            }

            int pi = slaves.get(i).slave.getPerformanceIndex();
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

            tryToProcess(slaves.get(i),new CodeExecutionRequest(numbersSlice, request.getRequestID(), request.getOp()),slaveHandler);
            //slaves.get(i).process(new CodeExecutionRequest(numbersSlice, request.getRequestID(), request.getOp()), slaveHandler);

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
        for(SlaveToSchedule s : unrequestedSlaves){
            tryToProcess(s,new CodeExecutionRequest(Collections.emptyList(),request.getRequestID(),request.getOp()),slaveHandler);
            //s.process(new CodeExecutionRequest(Collections.emptyList(),request.getRequestID(),request.getOp()),slaveHandler);
        }
    }

    private void scheduleReport(List<SlaveToSchedule> slaves, ReportPerformanceIndexRequest request, SlaveHandler slaveHandler){
        for(SlaveToSchedule s : slaves){
            tryToProcess(s,request,slaveHandler);
        }
    }

    @Override
    public void schedule(List<SlaveToSchedule> slaves, Request request, SlaveHandler slaveHandler) {
        if(request instanceof CodeExecutionRequest){
            scheduleComputation(slaves,(CodeExecutionRequest) request,slaveHandler);
        }else if(request instanceof ReportPerformanceIndexRequest){
            scheduleReport(slaves, (ReportPerformanceIndexRequest) request, slaveHandler);
        }
    }

    private void tryToProcess(SlaveToSchedule slave, Request request, SlaveHandler slaveHandler){
        if(slave.isAvailable){
            slave.slave.process(request,slaveHandler);
        } else{
            slaveHandler.reportCouldNotProcessRequest(slave.slave,request);
        }
    }
}
