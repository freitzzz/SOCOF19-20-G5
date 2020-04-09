package datastructures.list;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LockFreeList<T> extends ConcurrentLinkedQueue<T> {

    private final int maxSize;

    public LockFreeList(final int maxSize) {
        this.maxSize = maxSize;
    }

    // Might be inconsistent
    public boolean hasReachedMaxSize() {
        return this.size() == maxSize;
    }

}
