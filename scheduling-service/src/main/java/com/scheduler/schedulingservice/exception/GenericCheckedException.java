package com.scheduler.schedulingservice.exception;

import java.io.Serial;

public abstract class GenericCheckedException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    protected GenericCheckedException() {
    }

    protected GenericCheckedException(String message) {
        super(message);
    }

    protected GenericCheckedException(String message, Throwable cause) {
        super(message, cause);
    }

    protected GenericCheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
