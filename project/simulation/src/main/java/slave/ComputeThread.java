package slave;

import datastructures.Request;
import datastructures.Result;
import datastructures.handler.SlaveHandler;

import java.util.List;

public class ComputeThread extends Thread {

    private List<Integer> numbers;
    private SlaveHandler slaveMemory;
    private Request.Operation op;
    private int requestID;

    public ComputeThread(Request r, SlaveHandler slaveMemory){
        this.numbers= r.numbers;
        this.op = r.op;
        this.requestID = r.requestID;
        this.slaveMemory = slaveMemory;
    }

    @Override
    public void run() {
        super.run();

        Integer res;
        switch(op){
            case ADD: res = sum(); break;
            case MULTIPLY: res = mult(); break;
            default: throw new IllegalArgumentException("Operation unknown");
        }

        Result r = new Result(res,requestID);

        slaveMemory.pushResult(r);
    }

    private int sum() {
        Integer res = 0;
        for( Integer i : numbers){
            res+=i;
        }
        return res;
    }

    private int mult(){
        Integer res = 1;
        for( Integer i : numbers){
            res*=i;
        }
        return res;
    }
}