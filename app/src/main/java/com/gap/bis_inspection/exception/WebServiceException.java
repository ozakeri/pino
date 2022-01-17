package com.gap.bis_inspection.exception;

/**
 * Created by root on 9/14/15.
 */
public class WebServiceException extends Exception {

    public WebServiceException() {
        super();
    }

    public WebServiceException(String detailMessage) {
        super(detailMessage);
    }

    public WebServiceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public WebServiceException(Throwable throwable) {
        super(throwable);
    }
}
