package datastructures.list;

public class FixedSizeLockFreeList<T> extends LockFreeList<T> {

    private final int maxSize;

    public FixedSizeLockFreeList(final int maxSize) {
        this.maxSize = maxSize;
    }

    // Might be inconsistent
    public boolean hasReachedMaxSize() {
        return this.size() == maxSize;
    }

}
