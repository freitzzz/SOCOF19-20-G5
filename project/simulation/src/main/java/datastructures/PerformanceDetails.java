package datastructures;

import slave.Slave;

public class PerformanceDetails {

    public final Slave slave;

    public final int performanceIndex;

    public PerformanceDetails(final Slave slave, final int performanceIndex) {
        this.slave = slave;

        this.performanceIndex = performanceIndex;
    }

}
