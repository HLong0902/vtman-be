package com.viettel.vtman.cms.infrastructure.base.response;

public class Response<T> extends ResponseBase {
    private T output;

    public T getOutput() {
        return output;
    }

    public void setOutput(T output) {
        this.output = output;
    }
}
