package datastructures;

import java.util.List;
import java.util.Objects;

public class CodeExecutionRequest extends Request {

    private final List<Integer> numbers;

    private final Operation op;

    public CodeExecutionRequest(final List<Integer> numbers, final int requestID, final Operation op) {
        super(requestID);

        this.numbers = numbers;

        this.op = op;

    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public Operation getOp() {
        return op;
    }


    public enum Operation {
        ADD,
        MULTIPLY
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeExecutionRequest that = (CodeExecutionRequest) o;
        return Objects.equals(numbers, that.numbers) &&
                op == that.op;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers, op);
    }
}
