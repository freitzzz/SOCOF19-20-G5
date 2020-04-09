package datastructures;

import java.util.Objects;

public class Result {
    public final int value;
    public final int requestID;

    public int getValue() {
        return value;
    }

    public int getRequestID() {
        return requestID;
    }


    public Result(int value, int requestID) {
        this.value = value;
        this.requestID = requestID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return value == result.value &&
                requestID == result.requestID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, requestID);
    }
}
