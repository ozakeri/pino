package com.gap.bis_inspection.exception;

/**
 * Created by root on 9/14/15.
 */
public class InvalidDateTimeException extends Exception {

    public InvalidDateTimeException() {
        super();
    }

    public InvalidDateTimeException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidDateTimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InvalidDateTimeException(Throwable throwable) {
        super(throwable);
    }
}
