package slave;

import datastructures.ReportPerformanceIndexRequest;
import datastructures.handler.SlaveHandler;

public class ReportPerformanceIndexThread extends Thread {

    private final ReportPerformanceIndexRequest request;

    private final SlaveHandler slaveHandler;

    private final Slave slave;

    public ReportPerformanceIndexThread(final ReportPerformanceIndexRequest request, final SlaveHandler slaveHandler, final Slave slave) {
        this.request = request;
        this.slaveHandler = slaveHandler;
        this.slave = slave;
    }

    @Override
    public void run() {
        super.run();
        if (!isInterrupted()) {
            slaveHandler.reportPerformance(slave);
            slaveHandler.reportAvailability(slave, request);
        }
    }
}
