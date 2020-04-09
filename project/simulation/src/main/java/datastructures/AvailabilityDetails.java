package datastructures;

import slave.Slave;

public class AvailabilityDetails {

    public final Slave slave;

    public final int availability;

    public AvailabilityDetails(final Slave slave, final int availability) {
        this.slave = slave;

        this.availability = availability;
    }

}
