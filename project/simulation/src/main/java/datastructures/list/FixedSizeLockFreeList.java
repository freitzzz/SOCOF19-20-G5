package datastructures.list;

public class FixedSizeLockFreeList<T> extends LockFreeList<T> {

    private final int maxSize;

    public FixedSizeLockFreeList(final int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean hasReachedMaxSize() {
        return this.size() >= maxSize;
    }

}
