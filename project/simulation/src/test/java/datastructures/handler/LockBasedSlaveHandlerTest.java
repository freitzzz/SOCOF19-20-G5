package datastructures.handler;

import datastructures.CodeExecutionRequest;
import datastructures.scheduler.SlaveScheduler;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import slave.Slave;
import master.Master;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

public class LockBasedSlaveHandlerTest {

    private static final List<Slave> slaves = new ArrayList<>();

    private static final SlaveScheduler scheduler = mock(SlaveScheduler.class);

    private static final CodeExecutionRequest codeExecutionRequest = mock(CodeExecutionRequest.class);

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

        reset(codeExecutionRequest);
    }
    @Test
    public void ensureAllSlavesAreScheduledForComputeIfAllAllowAvailabilityReserveOnRequestComputation() {

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(100));
            when(slave.getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(25);
        });

        SlaveHandler slaveHandler = new LockBasedSlaveHandler(scheduler, master, slaves);

        slaveHandler.requestSlaves(codeExecutionRequest);

        slaves.forEach(slave -> {
            verify(slave, times(1)).getAvailability();
            verify(slave, times(1)).getAvailabilityReducePerCompute(codeExecutionRequest);
        });

        verify(scheduler, only()).schedule(slaves, codeExecutionRequest, slaveHandler);

        verify(master, never()).receiveRequestCouldNotBeScheduled(codeExecutionRequest);
    }
    @Test
    public void ensureSlaveIsNotScheduledIfAvailabilityWasNotReserved() {

        when(slaves.get(0).getAvailability()).thenReturn(new AtomicInteger(100));
        when(slaves.get(0).getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(25);

        when(slaves.get(1).getAvailability()).thenReturn(new AtomicInteger(55));
        when(slaves.get(1).getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(25);

        // Third slave cannot be reserved (45 - 55 < 0)

        when(slaves.get(2).getAvailability()).thenReturn(new AtomicInteger(44));
        when(slaves.get(2).getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(55);

        when(slaves.get(3).getAvailability()).thenReturn(new AtomicInteger(100));
        when(slaves.get(3).getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(99);

        when(slaves.get(4).getAvailability()).thenReturn(new AtomicInteger(100));
        when(slaves.get(4).getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(100);

        SlaveHandler slaveHandler = new LockBasedSlaveHandler(scheduler, master, slaves);

        slaveHandler.requestSlaves(codeExecutionRequest);

        List<Slave> expectedSlavesScheduledForCompute = new ArrayList<>();

        expectedSlavesScheduledForCompute.add(slaves.get(0));
        expectedSlavesScheduledForCompute.add(slaves.get(1));
        expectedSlavesScheduledForCompute.add(slaves.get(3));
        expectedSlavesScheduledForCompute.add(slaves.get(4));

        expectedSlavesScheduledForCompute.forEach(slave -> {
            verify(slave, times(1)).getAvailability();
            verify(slave, times(1)).getAvailabilityReducePerCompute(codeExecutionRequest);
        });

        verify(slaves.get(2), times(1)).getAvailability();
        verify(slaves.get(2), times(1)).getAvailabilityReducePerCompute(codeExecutionRequest);

        verify(scheduler, only()).schedule(expectedSlavesScheduledForCompute, codeExecutionRequest, slaveHandler);

        verify(master, never()).receiveRequestCouldNotBeScheduled(codeExecutionRequest);
    }


}
