package slave;

import datastructures.CodeExecutionRequest;
import datastructures.ReportPerformanceIndexRequest;
import datastructures.Request;
import datastructures.handler.SlaveHandler;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class SlaveTest {

    private TestSlaveHandler slaveHandler = new TestSlaveHandler(null);

    @Test
    public void ensureOnePlusOneIsTwo() {
        Slave slave = Mockito.spy(new Slave(1));

        Mockito.doReturn(false).when(slave).tryToRandomlyFail();

        List<Integer> l = new ArrayList<>();
        l.add(1); l.add(1);

        CodeExecutionRequest r = new CodeExecutionRequest(l,1,CodeExecutionRequest.Operation.ADD);

        slave.process(r, slaveHandler);
        assertEquals(slaveHandler.getResult(1).value,2);
    }

    @Test
    public void ensureTwoTimesTwoIsFour() {
        Slave slave = Mockito.spy(new Slave(1));

        Mockito.doReturn(false).when(slave).tryToRandomlyFail();

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(2);

        CodeExecutionRequest r = new CodeExecutionRequest(l,2,CodeExecutionRequest.Operation.MULTIPLY);

        slave.process(r, slaveHandler);
        assertEquals(slaveHandler.getResult(2).value,4);
    }

    @Test
    public void ensureTwoTimesZeroIsZero() {
        Slave slave = Mockito.spy(new Slave(1));

        Mockito.doReturn(false).when(slave).tryToRandomlyFail();

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(0);

        CodeExecutionRequest r = new CodeExecutionRequest(l,3,CodeExecutionRequest.Operation.MULTIPLY);

        slave.process(r, slaveHandler);
        assertEquals(slaveHandler.getResult(3).value,0);
    }

    @Test
    public void ensureMultipleRequestsAreOk(){
        Slave slave = Mockito.spy(new Slave(1));

        Mockito.doReturn(false).when(slave).tryToRandomlyFail();

        List<Integer> l = new ArrayList<>();
        l.add(2); l.add(0);

        CodeExecutionRequest r1 = new CodeExecutionRequest(l,4,CodeExecutionRequest.Operation.MULTIPLY);
        CodeExecutionRequest r2 = new CodeExecutionRequest(l,5,CodeExecutionRequest.Operation.MULTIPLY);
        CodeExecutionRequest r3 = new CodeExecutionRequest(l,6,CodeExecutionRequest.Operation.ADD);

        slave.process(r1, slaveHandler);
        slave.process(r2, slaveHandler);
        slave.process(r3, slaveHandler);
        assertEquals(slaveHandler.getResult(4).value,0);
        assertEquals(slaveHandler.getResult(5).value,0);
        assertEquals(slaveHandler.getResult(6).value,2);
    }

    @Test
    public void ensureCodeExecutionRequestCosts25AvailabilityPerCompute() {


        final Slave slave = new Slave(1);

        final Request request = new CodeExecutionRequest(new ArrayList<>(), 1, CodeExecutionRequest.Operation.ADD);

        final int expectedReduceOfAvailabilityPerCodeExecutionRequest = 25;

        final int actualReduceOfAvailabilityPerCodeExecutionRequest = slave.getAvailabilityReducePerCompute(request);

        assertEquals(expectedReduceOfAvailabilityPerCodeExecutionRequest, actualReduceOfAvailabilityPerCodeExecutionRequest);

    }

    @Test
    public void ensureReportPerformanceRequestCosts0AvailabilityPerCompute() {


        final Slave slave = new Slave(1);

        final Request request = new ReportPerformanceIndexRequest(1);

        final int expectedReduceOfAvailabilityPerCodeExecutionRequest = 0;

        final int actualReduceOfAvailabilityPerCodeExecutionRequest = slave.getAvailabilityReducePerCompute(request);

        assertEquals(expectedReduceOfAvailabilityPerCodeExecutionRequest, actualReduceOfAvailabilityPerCodeExecutionRequest);

    }

    @Test
    public void ensureIfSlaveRandomlyFailsSlaveHandlerIsNotifiedThatHeWasNotAbleToProcessTheRequest() {


        final Slave slave = Mockito.spy(new Slave(1));

        final Request request = new ReportPerformanceIndexRequest(1);

        final SlaveHandler slaveHandler = Mockito.mock(SlaveHandler.class);

        Mockito.doReturn(true).when(slave).tryToRandomlyFail();

        slave.process(request, slaveHandler);

        Mockito.verify(slaveHandler, Mockito.times(1)).reportCouldNotProcessRequest(slave, request);

    }

}