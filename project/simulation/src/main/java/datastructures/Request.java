package datastructures;

import java.util.List;
import java.util.Objects;

public class Request {
    private final List<Integer>numbers;
    private final int requestID;
    private final Operation op;

    public enum Operation {
        ADD,
        MULTIPLY
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public int getRequestID() {
        return requestID;
    }

    public Operation getOp() {
        return op;
    }

    public Request(List<Integer> numbers, int requestID, Operation op) {
        this.numbers = numbers;
        this.requestID = requestID;
        this.op = op;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return requestID == request.requestID &&
                numbers.size() == request.numbers.size() &&
                op == request.op;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers, requestID, op);
    }
}
