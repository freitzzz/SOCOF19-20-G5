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

    public ComputeThread(CodeExecutionRequest r, SlaveHandler slaveMemory, Slave s) {
        this.numbers = r.getNumbers();
        this.slaveMemory = slaveMemory;
        this.s = s;
        this.request = r;
    }

    @Override
    public void run() {
        super.run();

        Integer res;
        try {
            switch (request.getOp()) {
                case ADD:
                    res = sum();
                    break;
                case MULTIPLY:
                    res = mult();
                    break;
                default:
                    throw new IllegalArgumentException("Operation unknown");
            }

            if (!isInterrupted()) {
                Result r = new Result(res, request.getRequestID(), request.getOp());

                slaveMemory.pushResult(r);
                slaveMemory.reportAvailability(s, request);
            } else {
                slaveMemory.reportCouldNotProcessRequest(s, request);
            }

        } catch (InterruptedException ex) {
            slaveMemory.reportCouldNotProcessRequest(s, request);
        }


    }

    private int sum() throws InterruptedException {
        Integer res = 0;
        for (Integer i : numbers) {
            if (isInterrupted()) {
                throw new InterruptedException();
            }
            res += i;
        }
        return res;
    }

    private int mult() throws InterruptedException {
        Integer res = 1;
        for (Integer i : numbers) {
            if (isInterrupted()) {
                throw new InterruptedException();
            }
            res *= i;
        }
        return res;
    }
}
