package datastructures.handler;

import datastructures.Request;
import datastructures.Result;
import datastructures.scheduler.SlaveScheduler;
import master.Master;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import slave.Slave;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

public class LockFreeSlaveHandlerTest {

    private static final List<Slave> slaves = new ArrayList<>();

    private static final SlaveScheduler scheduler = mock(SlaveScheduler.class);

    private static final Request request = mock(Request.class);

    private static final Master master = mock(Master.class);

    @BeforeClass
    public static void setUp() {

        for(int i = 0; i < 5; i++) {

            slaves.add(mock(Slave.class));

        }

    }

    @After
    public void tearDown() {

        reset(slaves.toArray());

        reset(scheduler);

        reset(request);

        reset(master);
    }

    @Test
    public void ensureAllSlavesAreScheduledForComputeIfAllAllowAvailabilityReserveOnRequestComputation() {

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(100));
            when(slave.getAvailabilityReducePerCompute()).thenReturn(25);
        });

        SlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaveHandler.requestComputation(request);

        slaves.forEach(slave -> {
            verify(slave, times(2)).getAvailability();
            verify(slave, times(1)).getAvailabilityReducePerCompute();
        });

        verify(scheduler, only()).schedule(slaves, request);

        verify(master, never()).receiveRequestCouldNotBeScheduled(request);
    }

    @Test
    public void ensureSlaveIsNotScheduledIfAvailabilityWasNotReserved() {

        when(slaves.get(0).getAvailability()).thenReturn(new AtomicInteger(100));
        when(slaves.get(0).getAvailabilityReducePerCompute()).thenReturn(25);

        when(slaves.get(1).getAvailability()).thenReturn(new AtomicInteger(55));
        when(slaves.get(1).getAvailabilityReducePerCompute()).thenReturn(25);

        // Third slave cannot be reserved (45 - 55 < 0)

        when(slaves.get(2).getAvailability()).thenReturn(new AtomicInteger(44));
        when(slaves.get(2).getAvailabilityReducePerCompute()).thenReturn(55);

        when(slaves.get(3).getAvailability()).thenReturn(new AtomicInteger(100));
        when(slaves.get(3).getAvailabilityReducePerCompute()).thenReturn(99);

        when(slaves.get(4).getAvailability()).thenReturn(new AtomicInteger(100));
        when(slaves.get(4).getAvailabilityReducePerCompute()).thenReturn(100);

        SlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaveHandler.requestComputation(request);

        List<Slave> expectedSlavesScheduledForCompute = new ArrayList<>();

        expectedSlavesScheduledForCompute.add(slaves.get(0));
        expectedSlavesScheduledForCompute.add(slaves.get(1));
        expectedSlavesScheduledForCompute.add(slaves.get(3));
        expectedSlavesScheduledForCompute.add(slaves.get(4));

        expectedSlavesScheduledForCompute.forEach(slave -> {
            verify(slave, times(2)).getAvailability();
            verify(slave, times(1)).getAvailabilityReducePerCompute();
        });

        verify(slaves.get(2), times(1)).getAvailability();
        verify(slaves.get(2), times(1)).getAvailabilityReducePerCompute();

        verify(scheduler, only()).schedule(expectedSlavesScheduledForCompute, request);

        verify(master, never()).receiveRequestCouldNotBeScheduled(request);
    }

    @Test
    public void ensureNoSlavesAreNotScheduledForComputeIfNoSlaveIsAvailableToBeReserved() {

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(20));
            when(slave.getAvailabilityReducePerCompute()).thenReturn(25);
        });

        SlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaveHandler.requestComputation(request);

        slaves.forEach(slave -> {
            verify(slave, times(1)).getAvailability();
            verify(slave, times(1)).getAvailabilityReducePerCompute();
        });

        verify(scheduler, never()).schedule(any(List.class), any(Request.class));

        verify(master, only()).receiveRequestCouldNotBeScheduled(request);
    }

    @Test
    public void ensureWhenPushingResultsMasterDoesNotReceiveTheFinalResultIfScheduledSlavesHaventFinishCompute() {

        LockFreeSlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(100));
            when(slave.getAvailabilityReducePerCompute()).thenReturn(25);
        });

        when(request.getRequestID()).thenReturn(1);

        slaveHandler.requestComputation(request);

        Result result = mock(Result.class);

        when(result.getRequestID()).thenReturn(1);

        // don't push the result of the last slave

        for(int i = 0; i < slaves.size() - 1; i++) {
            slaveHandler.pushResult(result);
        }

        verify(result, times(slaves.size() - 1)).getRequestID();

        verify(master, never()).receiveResult(any(Result.class));

        // now push the last one

        slaveHandler.pushResult(result);

        verify(result, times(slaves.size() + 1)).getRequestID();

        verify(result, times(slaves.size())).getValue();

        verify(master, only()).receiveResult(any(Result.class));
    }

    @Test
    public void ensureMasterReceivesTheCorrectValueOfTheFinalResult() {

        LockFreeSlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(100));
            when(slave.getAvailabilityReducePerCompute()).thenReturn(25);
        });

        when(request.getRequestID()).thenReturn(1);

        slaveHandler.requestComputation(request);

        Result result = mock(Result.class);

        when(result.getRequestID()).thenReturn(1);

        when(result.getValue()).thenReturn(1);

        // don't push the result of the last slave

        for(int i = 0; i < slaves.size(); i++) {
            slaveHandler.pushResult(result);
        }

        Result finalResult = new Result(slaves.size() * 1, 1);

        verify(master, only()).receiveResult(finalResult);
    }
}