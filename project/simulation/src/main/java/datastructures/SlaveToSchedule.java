package datastructures;

import slave.Slave;

import java.util.Objects;

public class SlaveToSchedule implements Comparable<SlaveToSchedule>{

    public final Slave slave;

    public final boolean isAvailable;

    public SlaveToSchedule(final Slave slave, final boolean isAvailable) {
        this.slave = slave;
        this.isAvailable = isAvailable;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(slave, isAvailable);
    }

    @Override
    public int compareTo(SlaveToSchedule slave) {
        return Integer.compare(this.slave.getPerformanceIndex(), slave.slave.getPerformanceIndex());
    }
}
