package datastructures;

import slave.Slave;

public class SlaveToSchedule {

    public final Slave slave;

    public final boolean isAvailable;

    public SlaveToSchedule(final Slave slave, final boolean isAvailable) {
        this.slave = slave;
        this.isAvailable = isAvailable;
    }

}
