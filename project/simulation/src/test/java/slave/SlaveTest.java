package slave;

import datastructures.CodeExecutionRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class SlaveTest {

    private TestSlaveHandler slaveHandler = new TestSlaveHandler(null);

    @Test
    public void ensureOnePlusOneIsTwo() throws InterruptedException {
        Slave s = new Slave(1);

        List<Integer> l = new ArrayList<>();
        l.add(1); l.add(1);

        CodeExecutionRequest r = new CodeExecutionRequest(l,1,CodeExecutionRequest.Operation.ADD);

        s.process(r, slaveHandler);
        assertEquals(slaveHandler.getResult(1).value,2);
    }

    @Test
    public void ensureTwoTimesTwoIsFour() throws InterruptedException {
        Slave s = new Slave(1);

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(2);

        CodeExecutionRequest r = new CodeExecutionRequest(l,2,CodeExecutionRequest.Operation.MULTIPLY);

        s.process(r, slaveHandler);
        assertEquals(slaveHandler.getResult(2).value,4);
    }

    @Test
    public void ensureTwoTimesZeroIsZero() throws InterruptedException {
        Slave s = new Slave(1);

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(0);

        CodeExecutionRequest r = new CodeExecutionRequest(l,3,CodeExecutionRequest.Operation.MULTIPLY);

        s.process(r, slaveHandler);
        assertEquals(slaveHandler.getResult(3).value,0);
    }

    @Test
    public void ensureMultipleRequestsAreOk(){
        Slave s = new Slave(1);

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(0);

        CodeExecutionRequest r1 = new CodeExecutionRequest(l,4,CodeExecutionRequest.Operation.MULTIPLY);
        CodeExecutionRequest r2 = new CodeExecutionRequest(l,5,CodeExecutionRequest.Operation.MULTIPLY);
        CodeExecutionRequest r3 = new CodeExecutionRequest(l,6,CodeExecutionRequest.Operation.ADD);

        s.process(r1, slaveHandler);
        s.process(r2, slaveHandler);
        s.process(r3, slaveHandler);
        assertEquals(slaveHandler.getResult(4).value,0);
        assertEquals(slaveHandler.getResult(5).value,0);
        assertEquals(slaveHandler.getResult(6).value,2);
    }

}