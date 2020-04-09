package datastructures;

import slave.Slave;

import java.util.Objects;

public class PerformanceDetails {

    public final Slave slave;

    public final int performanceIndex;

    public PerformanceDetails(final Slave slave, final int performanceIndex) {
        this.slave = slave;

        this.performanceIndex = performanceIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerformanceDetails that = (PerformanceDetails) o;
        return performanceIndex == that.performanceIndex &&
                Objects.equals(slave, that.slave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slave, performanceIndex);
    }
}
