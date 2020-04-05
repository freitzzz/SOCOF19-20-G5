package main.java;

import java.util.List;

public class Request {
    public List<Integer>numbers;
    public int requestID;

    enum Operation {
        ADD,
        MULTIPLY
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public Request(List<Integer> numbers, int requestID) {
        this.numbers = numbers;
        this.requestID = requestID;
    }
}
