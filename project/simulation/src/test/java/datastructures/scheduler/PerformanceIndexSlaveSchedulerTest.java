package datastructures.scheduler;

import datastructures.Request;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import slave.Slave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;

public class PerformanceIndexSlaveSchedulerTest {

    final static List<Slave> slaves = new ArrayList<>();

    final static Request request = mock(Request.class);

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
        reset(request);
    }

    @Test
    public void ensureEachSlaveComputesRespectiveNumbersSlice() {

        when(slaves.get(0).getPerformanceIndex()).thenReturn(1);
        when(slaves.get(1).getPerformanceIndex()).thenReturn(2);
        when(slaves.get(2).getPerformanceIndex()).thenReturn(3);
        when(slaves.get(3).getPerformanceIndex()).thenReturn(4);
        when(slaves.get(4).getPerformanceIndex()).thenReturn(5);

        Request requestToCompute = new Request(Arrays.asList(new Integer[]{1, 2 , 3, 4, 5}), 1, Request.Operation.ADD);

        when(request.getNumbers()).thenReturn(requestToCompute.getNumbers());

        when(request.getRequestID()).thenReturn(requestToCompute.getRequestID());

        when(request.getOp()).thenReturn(requestToCompute.getOp());

        int startIndex = 0;

        final List<Request> expectedSlaveRequestsList = new ArrayList<>();

        expectedSlaveRequestsList.add(new Request(requestToCompute.getNumbers().subList(0, 2),requestToCompute.getRequestID(),requestToCompute.getOp()));
        expectedSlaveRequestsList.add(new Request(requestToCompute.getNumbers().subList(2, 5),requestToCompute.getRequestID(),requestToCompute.getOp()));

        PerformanceIndexSlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();

        scheduler.schedule(slaves, request);

        verify(request, times(1+2)).getNumbers();

        verify(request, times(2)).getRequestID();

        verify(request, times(2)).getOp();

        verify(slaves.get(3)).compute(expectedSlaveRequestsList.get(0));
        verify(slaves.get(4)).compute(expectedSlaveRequestsList.get(1));


        for(int i = 0; i < 3; i++) {
            verify(slaves.get(i),never()).compute(any(Request.class));
        }

    }

    @Test
    public void ensureOneSlaveComputesAllNumbersWhenOnlyOneNumberRemaining() {

        when(slaves.get(0).getPerformanceIndex()).thenReturn(5);
        when(slaves.get(1).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(2).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(3).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(4).getPerformanceIndex()).thenReturn(2);

        Request requestToCompute = new Request(Arrays.asList(new Integer[]{1, 2 , 3}), 1, Request.Operation.ADD);

        when(request.getNumbers()).thenReturn(requestToCompute.getNumbers());

        when(request.getRequestID()).thenReturn(requestToCompute.getRequestID());

        when(request.getOp()).thenReturn(requestToCompute.getOp());

        int startIndex = 0;

        final List<Request> expectedSlaveRequestsList = new ArrayList<>();

        expectedSlaveRequestsList.add(new Request(requestToCompute.getNumbers(),requestToCompute.getRequestID(),requestToCompute.getOp()));
        //expectedSlaveRequestsList.add(new Request(requestToCompute.getNumbers().subList(2, 5),requestToCompute.getRequestID(),requestToCompute.getOp()));

        PerformanceIndexSlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();

        scheduler.schedule(slaves, request);

        verify(request, times(1+1)).getNumbers();

        verify(request, times(1)).getRequestID();

        verify(request, times(1)).getOp();

        verify(slaves.get(0)).compute(expectedSlaveRequestsList.get(0));


        for(int i = 1; i < 5; i++) {
            verify(slaves.get(i),never()).compute(any(Request.class));
        }

    }

    @Test
    public void ensureOneSlaveComputesAllNumbersWhenOnlyOneSlaveAvailable() {

        when(slaves.get(0).getPerformanceIndex()).thenReturn(5);
        when(slaves.get(1).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(2).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(3).getPerformanceIndex()).thenReturn(0);
        when(slaves.get(4).getPerformanceIndex()).thenReturn(0);

        Request requestToCompute = new Request(Arrays.asList(new Integer[]{1, 2 , 3}), 1, Request.Operation.ADD);

        when(request.getNumbers()).thenReturn(requestToCompute.getNumbers());

        when(request.getRequestID()).thenReturn(requestToCompute.getRequestID());

        when(request.getOp()).thenReturn(requestToCompute.getOp());

        int startIndex = 0;

        final List<Request> expectedSlaveRequestsList = new ArrayList<>();

        expectedSlaveRequestsList.add(new Request(requestToCompute.getNumbers(),requestToCompute.getRequestID(),requestToCompute.getOp()));
        //expectedSlaveRequestsList.add(new Request(requestToCompute.getNumbers().subList(2, 5),requestToCompute.getRequestID(),requestToCompute.getOp()));

        PerformanceIndexSlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();

        scheduler.schedule(slaves, request);

        verify(request, times(1+1)).getNumbers();

        verify(request, times(1)).getRequestID();

        verify(request, times(1)).getOp();

        verify(slaves.get(0)).compute(expectedSlaveRequestsList.get(0));


        for(int i = 1; i < 5; i++) {
            verify(slaves.get(i),never()).compute(any(Request.class));
        }

    }

}