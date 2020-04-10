package slave;

import org.junit.Test;

import datastructures.Request;
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

        Request r = new Request(l,1,Request.Operation.ADD);

        s.compute(r, slaveHandler);
        assertEquals(slaveHandler.getResult(1).value,2);
    }

    @Test
    public void ensureTwoTimesTwoIsFour() throws InterruptedException {
        Slave s = new Slave(1);

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(2);

        Request r = new Request(l,2,Request.Operation.MULTIPLY);

        s.compute(r, slaveHandler);
        assertEquals(slaveHandler.getResult(2).value,4);
    }

    @Test
    public void ensureTwoTimesZeroIsZero() throws InterruptedException {
        Slave s = new Slave(1);

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(0);

        Request r = new Request(l,3,Request.Operation.MULTIPLY);

        s.compute(r, slaveHandler);
        assertEquals(slaveHandler.getResult(3).value,0);
    }

    @Test
    public void ensureMultipleRequestsAreOk(){
        Slave s = new Slave(1);

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(0);

        Request r1 = new Request(l,4,Request.Operation.MULTIPLY);
        Request r2 = new Request(l,5,Request.Operation.MULTIPLY);
        Request r3 = new Request(l,6,Request.Operation.ADD);

        s.compute(r1, slaveHandler);
        s.compute(r2, slaveHandler);
        s.compute(r3, slaveHandler);
        assertEquals(slaveHandler.getResult(4).value,0);
        assertEquals(slaveHandler.getResult(5).value,0);
        assertEquals(slaveHandler.getResult(6).value,2);
    }

}