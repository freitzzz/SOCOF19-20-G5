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
    private Slave s;

    public ComputeThread(Request r, SlaveHandler slaveMemory,Slave s){
        this.numbers= r.getNumbers();
        this.op = r.getOp();
        this.requestID = r.getRequestID();
        this.slaveMemory = slaveMemory;
        this.s = s;
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
        slaveMemory.reportAvailability(s);
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
