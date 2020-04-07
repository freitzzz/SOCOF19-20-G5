package slave;

import datastructures.Request;
import datastructures.Result;
import datastructures.handler.SlaveHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestSlaveHandler implements SlaveHandler {
    private ConcurrentLinkedQueue<Result> results = new ConcurrentLinkedQueue<>();


    @Override
    public void requestComputation(Request request) {

    }

    @Override
    public  void pushResult(Result result) {
        this.results.add(result);
    }

    @Override
    public void reportPerformance(Slave slave, int index) {

    }

    @Override
    public void reportAvailability(Slave slave, double availability) {

    }

    public Result getResult(int id){
        while(true){
            for(Result r : results){
                if(r.requestID == id){
                    return r;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
