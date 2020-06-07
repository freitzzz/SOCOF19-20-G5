package datastructures.scheduler;

import datastructures.CodeExecutionRequest;
import datastructures.Request;
import datastructures.SlaveToSchedule;
import datastructures.handler.SlaveHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import slave.Slave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

public class PerformanceIndexSlaveSchedulerTest {

    final static List<Slave> slaves = new ArrayList<>();

    final static CodeExecutionRequest codeExecutionRequest = mock(CodeExecutionRequest.class);

    final static SlaveHandler slaveHandler = mock(SlaveHandler.class);

    @BeforeClass
    public static void setUp() {
        for(int i = 0; i < 5; i++) {
            final Slave mockSlave = mock(Slave.class);

            slaves.add(mockSlave);
        }

    }

    @After
    public void tearDown() {
        for(Slave s : slaves){
            reset(s);
        }
        reset(codeExecutionRequest);
    }

    @Test
    public void ensureEachSlaveComputesRespectiveNumbersSlice() {

        when(slaves.get(0).getPerformanceIndex()).thenReturn(1);
        when(slaves.get(1).getPerformanceIndex()).thenReturn(2);
        when(slaves.get(2).getPerformanceIndex()).thenReturn(3);
        when(slaves.get(3).getPerformanceIndex()).thenReturn(4);
        when(slaves.get(4).getPerformanceIndex()).thenReturn(5);

        CodeExecutionRequest requestToCompute = new CodeExecutionRequest(Arrays.asList(new Integer[]{1, 2 , 3, 4, 5}), 1, CodeExecutionRequest.Operation.ADD);

        when(codeExecutionRequest.getNumbers()).thenReturn(requestToCompute.getNumbers());

        when(codeExecutionRequest.getRequestID()).thenReturn(requestToCompute.getRequestID());

        when(codeExecutionRequest.getOp()).thenReturn(requestToCompute.getOp());

        final List<Request> expectedSlaveRequestsList = new ArrayList<>();

        expectedSlaveRequestsList.add(new CodeExecutionRequest(requestToCompute.getNumbers().subList(0, 2),requestToCompute.getRequestID(),requestToCompute.getOp()));
        expectedSlaveRequestsList.add(new CodeExecutionRequest(requestToCompute.getNumbers().subList(2, 5),requestToCompute.getRequestID(),requestToCompute.getOp()));

        PerformanceIndexSlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();

        List<SlaveToSchedule> fSlaves = slaves.parallelStream().map(s -> new SlaveToSchedule(s,true)).collect(Collectors.toList());
        scheduler.schedule(fSlaves, codeExecutionRequest, slaveHandler);

        verify(codeExecutionRequest, times(1+2)).getNumbers();

        //verify(codeExecutionRequest, times(2)).getRequestID();

        //verify(codeExecutionRequest, times(2)).getOp();

        verify(slaves.get(3)).process(expectedSlaveRequestsList.get(0), slaveHandler);
        verify(slaves.get(4)).process(expectedSlaveRequestsList.get(1), slaveHandler);


        /*for(int i = 0; i < 3; i++) {
            verify(slaves.get(i),never()).process(any(Request.class), any(SlaveHandler.class));
        }*/

    }

    @Test
    public void ensureOneSlaveComputesAllNumbersWhenOnlyOneNumberRemaining() {

        when(slaves.get(0).getPerformanceIndex()).thenReturn(5);
        when(slaves.get(1).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(2).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(3).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(4).getPerformanceIndex()).thenReturn(2);

        CodeExecutionRequest requestToCompute = new CodeExecutionRequest(Arrays.asList(new Integer[]{1, 2 , 3}), 1, CodeExecutionRequest.Operation.ADD);

        when(codeExecutionRequest.getNumbers()).thenReturn(requestToCompute.getNumbers());

        when(codeExecutionRequest.getRequestID()).thenReturn(requestToCompute.getRequestID());

        when(codeExecutionRequest.getOp()).thenReturn(requestToCompute.getOp());

        final List<Request> expectedSlaveRequestsList = new ArrayList<>();

        expectedSlaveRequestsList.add(new CodeExecutionRequest(requestToCompute.getNumbers(),requestToCompute.getRequestID(),requestToCompute.getOp()));

        PerformanceIndexSlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();

        List<SlaveToSchedule> fSlaves = slaves.parallelStream().map(s -> new SlaveToSchedule(s,true)).collect(Collectors.toList());

        scheduler.schedule(fSlaves, codeExecutionRequest, slaveHandler);

        verify(codeExecutionRequest, times(1+1)).getNumbers();

        //verify(codeExecutionRequest, times(1)).getRequestID();

        //verify(codeExecutionRequest, times(1)).getOp();

        verify(slaves.get(0)).process(expectedSlaveRequestsList.get(0), slaveHandler);


        /*for(int i = 1; i < 5; i++) {
            verify(slaves.get(i),never()).process(any(Request.class), any(SlaveHandler.class));
        }*/

    }

    @Test
    public void ensureOneSlaveComputesAllNumbersWhenOnlyOneSlaveAvailable() {

        when(slaves.get(0).getPerformanceIndex()).thenReturn(5);
        when(slaves.get(1).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(2).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(3).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(4).getPerformanceIndex()).thenReturn(0);

        CodeExecutionRequest requestToCompute = new CodeExecutionRequest(Arrays.asList(new Integer[]{1, 2 , 3}), 1, CodeExecutionRequest.Operation.ADD);

        when(codeExecutionRequest.getNumbers()).thenReturn(requestToCompute.getNumbers());

        when(codeExecutionRequest.getRequestID()).thenReturn(requestToCompute.getRequestID());

        when(codeExecutionRequest.getOp()).thenReturn(requestToCompute.getOp());

        final List<Request> expectedSlaveRequestsList = new ArrayList<>();

        expectedSlaveRequestsList.add(new CodeExecutionRequest(requestToCompute.getNumbers(),requestToCompute.getRequestID(),requestToCompute.getOp()));

        PerformanceIndexSlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();


        List<SlaveToSchedule> fSlaves = slaves.parallelStream().map(s -> new SlaveToSchedule(s,true)).collect(Collectors.toList());
        scheduler.schedule(fSlaves, codeExecutionRequest, slaveHandler);

        verify(codeExecutionRequest, times(1+1)).getNumbers();

        //verify(codeExecutionRequest, times(1)).getRequestID();

        //verify(codeExecutionRequest, times(1)).getOp();

        verify(slaves.get(0)).process(expectedSlaveRequestsList.get(0), slaveHandler);


        /*for(int i = 1; i < 5; i++) {
            verify(slaves.get(i),never()).process(any(Request.class), any(SlaveHandler.class));
        }*/

    }

    @Test
    public void ensureSlaveSchedulerDividesAllNumbersCorrectly() {

        List<Slave> slaves = new ArrayList<>();

        AtomicInteger numbersInvokedCount = new AtomicInteger();

        for(int i = 0; i < 1; i++) {

            final Slave slave = mock(Slave.class);

            when(slave.getPerformanceIndex()).thenReturn(i + 1);

            slaves.add(slave);

            doAnswer(invocation -> {
                CodeExecutionRequest request = (CodeExecutionRequest)invocation.getArgument(0);
                numbersInvokedCount.addAndGet(request.getNumbers().size());
                return null;
            }).when(slaves.get(i)).process(any(Request.class), any(SlaveHandler.class));

        }

        SlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();

        for(int i = 0; i < 5000; i++) {

            final List<Integer> requestNumbers = new ArrayList<>();

            Random random = new Random();

            final int requestNumbersSize = random.nextInt(1000);

            for (int j = 0; j < requestNumbersSize; j++) {

                requestNumbers.add(j);

            }

            when(codeExecutionRequest.getRequestID()).thenReturn(1l);

            when(codeExecutionRequest.getOp()).thenReturn(CodeExecutionRequest.Operation.ADD);

            when(codeExecutionRequest.getNumbers()).thenReturn(requestNumbers);

            List<SlaveToSchedule> fSlaves = slaves.parallelStream().map(s -> new SlaveToSchedule(s,true)).collect(Collectors.toList());
            scheduler.schedule(fSlaves, codeExecutionRequest, slaveHandler);

            Assert.assertEquals(requestNumbersSize, numbersInvokedCount.get());

            numbersInvokedCount.set(0);
        }
    }

}