package datastructures;

import java.util.Objects;

public abstract class Request {

    private final long requestID;

    public long getRequestID() {
        return requestID;
    }

    public Request(final long requestID) {
        this.requestID = requestID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return requestID == request.requestID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestID);
    }
}
