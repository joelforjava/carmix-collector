package com.joelforjava.service.response;

import com.joelforjava.request.CopyRequest;

public class CopyResponse {

    private final CopyRequest request;
    private final ResponseStatus responseStatus;

    public CopyResponse(CopyRequest request, ResponseStatus responseStatus) {
        this.request = request;
        this.responseStatus = responseStatus;
    }

    public CopyRequest getRequest() {
        return this.request;
    }

    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public enum ResponseStatus {
        SUCCESS, SKIPPED, FAILED, OVERWRITTEN
    }
}
