package datastructures.list;

import java.util.LinkedList;

public class LockBasedList<T> extends LinkedList<T> {

    private final int maxSize;

    public LockBasedList(final int maxSize) {
        this.maxSize = maxSize;
    }

    // Might be inconsistent
    public boolean hasReachedMaxSize() {
        return this.size() == maxSize;
    }
}
