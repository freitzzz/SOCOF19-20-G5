package datastructures.handler;

import datastructures.*;
import datastructures.scheduler.SlaveScheduler;
import master.Master;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationMode;
import slave.Slave;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LockFreeSlaveHandlerTest {

    private static final List<Slave> slaves = new ArrayList<>();

    private static final SlaveScheduler scheduler = mock(SlaveScheduler.class);

    private static final CodeExecutionRequest codeExecutionRequest = mock(CodeExecutionRequest.class);

    private static final ReportPerformanceIndexRequest reportPerformanceIndexRequest = mock(ReportPerformanceIndexRequest.class);

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

        reset(reportPerformanceIndexRequest);

        reset(master);
    }

    @Test
    public void ensureAllSlavesAreScheduledForComputeIfAllAllowAvailabilityReserveOnRequestComputation() {

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(100));
            when(slave.getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(25);
        });

        SlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaveHandler.requestSlaves(codeExecutionRequest);

        slaves.forEach(slave -> {
            verify(slave, times(2)).getAvailability();
            verify(slave, times(1)).getAvailabilityReducePerCompute(codeExecutionRequest);
        });

        final List<SlaveToSchedule> slavesToSchedule = new ArrayList<>();

        for (Slave slave : slaves) {
            slavesToSchedule.add(new SlaveToSchedule(slave, true));
        }

        verify(scheduler, times(1)).schedule(slavesToSchedule, codeExecutionRequest, slaveHandler);

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

        SlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaveHandler.requestSlaves(codeExecutionRequest);

        final List<SlaveToSchedule> expectedSlavesScheduledForCompute = new ArrayList<>();

        expectedSlavesScheduledForCompute.add(new SlaveToSchedule(slaves.get(0), true));
        expectedSlavesScheduledForCompute.add(new SlaveToSchedule(slaves.get(1), true));
        expectedSlavesScheduledForCompute.add(new SlaveToSchedule(slaves.get(2), false));
        expectedSlavesScheduledForCompute.add(new SlaveToSchedule(slaves.get(3), true));
        expectedSlavesScheduledForCompute.add(new SlaveToSchedule(slaves.get(4), true));

        expectedSlavesScheduledForCompute.
                parallelStream()
                .filter(slaveToSchedule -> slaveToSchedule.slave != slaves.get(2))
                .forEach(slaveToSchedule -> {
                    verify(slaveToSchedule.slave, times(2)).getAvailability();
                    verify(slaveToSchedule.slave, times(1)).getAvailabilityReducePerCompute(codeExecutionRequest);
                });

        verify(slaves.get(2), times(1)).getAvailability();
        verify(slaves.get(2), times(1)).getAvailabilityReducePerCompute(codeExecutionRequest);

        verify(scheduler, times(1)).schedule(expectedSlavesScheduledForCompute, codeExecutionRequest, slaveHandler);

        verify(master, never()).receiveRequestCouldNotBeScheduled(codeExecutionRequest);
    }

    @Test
    public void ensureNoSlavesAreNotScheduledForComputeIfNoSlaveIsAvailableToBeReserved() {

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(20));
            when(slave.getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(25);
        });

        SlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaveHandler.requestSlaves(codeExecutionRequest);

        slaves.forEach(slave -> {
            verify(slave, times(1)).getAvailability();
            verify(slave, times(1)).getAvailabilityReducePerCompute(codeExecutionRequest);
        });

        final List<SlaveToSchedule> slavesToSchedule = new ArrayList<>();

        for (Slave slave : slaves) {
            slavesToSchedule.add(new SlaveToSchedule(slave, false));
        }

        verify(scheduler, times(1)).schedule(slavesToSchedule, codeExecutionRequest, slaveHandler);
    }

    @Test
    public void ensureWhenPushingResultsMasterDoesNotReceiveTheFinalResultIfScheduledSlavesHaventFinishCompute() {

        LockFreeSlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(100));
            when(slave.getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(25);
        });

        when(codeExecutionRequest.getRequestID()).thenReturn(1);

        slaveHandler.requestSlaves(codeExecutionRequest);

        Result result = mock(Result.class);

        when(result.getRequestID()).thenReturn(1);
        when(result.getOperation()).thenReturn(CodeExecutionRequest.Operation.ADD);

        slaveHandler.notifyScheduledRequests(codeExecutionRequest, slaves.size());

        // don't push the result of the last slave

        for(int i = 0; i < slaves.size() - 1; i++) {
            slaveHandler.pushResult(result);
        }

        verify(result, times(slaves.size() - 1)).getRequestID();

        verify(master, never()).receiveResult(any(Result.class));

        // now push the last one

        slaveHandler.pushResult(result);

        verify(result, times(slaves.size() + 2)).getRequestID();

        verify(result, times(slaves.size())).getValue();

        verify(master, times(1)).receiveResult(any(Result.class));
    }

    @Test
    public void ensureOnNotifyScheduledRequestIfTheRequestIsCodeExecutionRequestThenTheAmountOfSchedulesIsAllocatedInComputationResultsMap() {

        LockFreeSlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        assertNull(slaveHandler.computationResults.get(codeExecutionRequest.getRequestID()));

        slaveHandler.notifyScheduledRequests(codeExecutionRequest, 5);

        assertNotNull(slaveHandler.computationResults.get(codeExecutionRequest.getRequestID()));


    }

    @Test
    public void ensureOnReportCouldNotProcessRequestIfTheRequestIsReportPerformanceIndexRequestThenTheRequestIsRescheduledInTheFuture() {

        LockFreeSlaveHandler slaveHandler = spy(new LockFreeSlaveHandler(scheduler, master, slaves));

        final Slave slave = slaves.get(0);

        final Request request = reportPerformanceIndexRequest;

        slaveHandler.reportCouldNotProcessRequest(slave, request);

        verify(slaveHandler, times(1)).rescheduleRequestToSlaveInTheFuture(slave, request);

    }

    @Test
    public void ensureOnReportCouldNotProcessRequestIfTheRequestIsCodeExecutionRequestAndThereIsAtLeastTwoSlavesForScheduleThenTheRequestIsScheduleToOneOfThese() {

        LockFreeSlaveHandler slaveHandler = spy(new LockFreeSlaveHandler(scheduler, master, slaves));

        slaves.forEach(slave -> when(slave.getAvailability()).thenReturn(new AtomicInteger(100)));

        final Slave slave = slaves.get(0);

        final Request request = codeExecutionRequest;

        slaveHandler.reportCouldNotProcessRequest(slave, request);

        verify(slave, never()).process(request, slaveHandler);

        final int[] numberOfTimesProcessCalled = {0};

        slaves.parallelStream()
                .filter(slave1 -> slave1 != slave)
                .forEach(slave1 -> verify(slave1, new VerificationMode() {
                    @Override
                    public void verify(VerificationData data) {
                        if(data.getAllInvocations().size() > 0) {
                            numberOfTimesProcessCalled[0]++;
                        }
                    }

                    @Override
                    public VerificationMode description(String description) {
                        return null;
                    }
                }).process(request, slaveHandler));

        assertEquals(numberOfTimesProcessCalled[0], 1);
    }

    @Test
    public void ensureOnReportCouldNotProcessRequestIfTheRequestIsCodeExecutionRequestAndThereIsOnlyOneSlaveForScheduleThenTheRequestIsRescheduledInTheFutureForTheSlave() {

        final List<Slave> availableSlaves = slaves.subList(0, 1);

        LockFreeSlaveHandler slaveHandler = spy(new LockFreeSlaveHandler(scheduler, master, availableSlaves));

        final Slave slave = availableSlaves.get(0);

        final Request request = codeExecutionRequest;

        slaveHandler.reportCouldNotProcessRequest(slave, request);

        verify(slaveHandler, times(1)).rescheduleRequestToSlaveInTheFuture(slave, request);
    }

    @Test
    public void ensureMasterReceivesTheCorrectValueOfTheFinalResult() {

        LockFreeSlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        slaves.forEach(slave -> {
            when(slave.getAvailability()).thenReturn(new AtomicInteger(100));
            when(slave.getAvailabilityReducePerCompute(codeExecutionRequest)).thenReturn(25);
        });

        when(codeExecutionRequest.getRequestID()).thenReturn(1);

        slaveHandler.requestSlaves(codeExecutionRequest);

        Result result = mock(Result.class);

        when(result.getRequestID()).thenReturn(1);

        when(result.getValue()).thenReturn(1);
        when(result.getOperation()).thenReturn(CodeExecutionRequest.Operation.ADD);

        slaveHandler.notifyScheduledRequests(codeExecutionRequest, slaves.size());

        for(int i = 0; i < slaves.size(); i++) {
            slaveHandler.pushResult(result);
        }

        Result finalResult = new Result(slaves.size() * 1, 1,CodeExecutionRequest.Operation.ADD);

        verify(master, times(1)).receiveResult(finalResult);
    }

    @Test
    public void ensureMasterReceivesSlavePerformanceDetails() {

        Slave slave = slaves.get(0);

        when(slave.getPerformanceIndex()).thenReturn(1);

        SlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        PerformanceDetails expectedPerformanceDetails = new PerformanceDetails(slave, slave.getPerformanceIndex());

        slaveHandler.reportPerformance(slave);

        verify(master, times(1)).receiveSlavePerformanceDetails(expectedPerformanceDetails);

    }

    @Test
    public void ensureMasterReceivesSlaveAvailabilityDetails() {

        Slave slave = slaves.get(0);

        when(slave.getAvailability()).thenReturn(new AtomicInteger(1));

        SlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        AvailabilityDetails expectedAvailabilityDetails = new AvailabilityDetails(slave, slave.getAvailability().intValue());

        slaveHandler.reportAvailability(slave, codeExecutionRequest);

        verify(master, times(1)).receiveSlaveAvailability(expectedAvailabilityDetails);

    }

    // TODO: Uncomment once performance index slave scheduler is completed
    /*@Test
    public void ensureSlaveHandlerHandlesConcurrentCallsAsExpected() {

        List<Slave> slaves = new ArrayList<>();

        Master master = mock(Master.class);

        SlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();

        for(int i = 0; i < 5; i++) {

            slaves.add(new Slave(i + 1));

        }

        LockFreeSlaveHandler slaveHandler = new LockFreeSlaveHandler(scheduler, master, slaves);

        final List<Integer> values = new ArrayList<>();

        for(int i = 0; i < 100000; i++) {
            values.add(i);
        }

        final int expectedSum = values.parallelStream().reduce(0, (integer, integer2) -> integer + integer2);

        doAnswer(invocation -> {

            slaveHandler.requestSlaves(invocation.getArgument(0));

            return null;
        }).when(master).receiveRequestCouldNotBeScheduled(any(Request.class));

        doAnswer(invocation -> {

            return null;
        }).when(master).receiveSlaveAvailability(any(AvailabilityDetails.class));

        final AtomicInteger timesInvoked = new AtomicInteger();

        doAnswer(invocation -> {
            timesInvoked.getAndIncrement();
            Result result = invocation.getArgument(0);
            if(result.getValue() != expectedSum) {
                throw new Exception();
            }
            return null;
        }).when(master).receiveResult(any(Result.class));

        for(int i = 0; i < 10000; i++) {
            slaveHandler.requestSlaves(new CodeExecutionRequest(values, i, CodeExecutionRequest.Operation.ADD));
        }

        ((Runnable) () -> {
            while (timesInvoked.intValue() < 10000) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).run();
    }*/
}