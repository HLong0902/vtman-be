package com.viettel.vtman.cms.infrastructure.base.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ErrorResponse extends ResponseBase {

    private List<String> output;
    private int statusCode;

    public ErrorResponse(String messageError, List<String> output, int statusCode) {
        this.output = Objects.isNull(output) ? null : new ArrayList<>(output);
        this.setSuccess(false);
        this.setMessage(messageError);
        this.setStatusCode(statusCode);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getOutput() {
        return Objects.isNull(output) ? null : new ArrayList<>(output);
    }

    public void setOutput(List<String> output) {
        this.output = Objects.isNull(output) ? null : new ArrayList<>(output);
    }
}
