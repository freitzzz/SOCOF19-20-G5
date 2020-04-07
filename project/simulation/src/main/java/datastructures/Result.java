package datastructures;

public class Result {
    public int value;
    public int requestID;

    public int getValue() {
        return value;
    }

    public int getRequestID() {
        return requestID;
    }


    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Result(int value, int requestID) {
        this.value = value;
        this.requestID = requestID;
    }
}
