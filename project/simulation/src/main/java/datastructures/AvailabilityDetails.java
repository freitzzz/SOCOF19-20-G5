package datastructures;

import slave.Slave;

import java.util.Objects;

public class AvailabilityDetails {

    public final Slave slave;

    public final int availability;

    public AvailabilityDetails(final Slave slave, final int availability) {
        this.slave = slave;

        this.availability = availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailabilityDetails that = (AvailabilityDetails) o;
        return availability == that.availability &&
                Objects.equals(slave, that.slave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slave, availability);
    }
}
