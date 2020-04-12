package slave;

import datastructures.CodeExecutionRequest;
import datastructures.Request;
import datastructures.Result;
import datastructures.handler.SlaveHandler;

import java.util.List;

public class ComputeThread extends Thread {

    private List<Integer> numbers;
    private SlaveHandler slaveMemory;
    private CodeExecutionRequest request;
    private Slave s;

    public ComputeThread(CodeExecutionRequest r, SlaveHandler slaveMemory,Slave s){
        this.numbers= r.getNumbers();
        this.slaveMemory = slaveMemory;
        this.s = s;
        this.request = r;
    }

    @Override
    public void run() {
        super.run();

        Integer res;
        switch(request.getOp()){
            case ADD: res = sum(); break;
            case MULTIPLY: res = mult(); break;
            default: throw new IllegalArgumentException("Operation unknown");
        }

        Result r = new Result(res,request.getRequestID(),request.getOp());

        slaveMemory.pushResult(r);
        slaveMemory.reportAvailability(s, request);
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
