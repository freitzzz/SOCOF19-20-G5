package datastructures.scheduler;

import datastructures.Request;
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

            when(mockSlave.getPerformanceIndex()).thenReturn(i + 1);

            slaves.add(mockSlave);
        }
    }

    @Test
    public void ensureEachSlaveComputesRespectiveNumbersSlice() {

        Request requestToCompute = new Request(Arrays.asList(new Integer[]{1, 2 , 3, 4, 5}), 1, Request.Operation.ADD);

        when(request.getNumbers()).thenReturn(requestToCompute.getNumbers());

        when(request.getRequestID()).thenReturn(requestToCompute.getRequestID());

        when(request.getOp()).thenReturn(requestToCompute.getOp());

        int startIndex = 0;

        final List<Request> expectedSlaveRequestsList = new ArrayList<>();

        for(int i = 0; i < slaves.size(); i++) {

            final int expectedFormulaResult = (int) (requestToCompute.getNumbers().size() * (Double.valueOf(slaves.get(i).getPerformanceIndex() * slaves.size() ) / 100));

            System.out.println(requestToCompute.getNumbers().size() * (Double.valueOf(slaves.get(i).getPerformanceIndex() * slaves.size() ) / 100));

            List<Integer> expectedNumbersSlice = requestToCompute.getNumbers().subList(startIndex, expectedFormulaResult);

            startIndex = expectedFormulaResult;

            System.out.println(expectedNumbersSlice);

            expectedSlaveRequestsList.add(new Request(expectedNumbersSlice, requestToCompute.getRequestID(), requestToCompute.getOp()));
        }

        PerformanceIndexSlaveScheduler scheduler = new PerformanceIndexSlaveScheduler();

        scheduler.schedule(slaves, request);

        verify(request, times(slaves.size() * 2)).getNumbers();

        verify(request, times(slaves.size())).getRequestID();

        verify(request, times(slaves.size())).getOp();

        for(int i = 0; i < slaves.size(); i++) {

            verify(slaves.get(i)).compute(expectedSlaveRequestsList.get(i));

        }

    }

}