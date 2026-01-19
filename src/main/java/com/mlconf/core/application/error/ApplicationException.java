package com.mlconf.core.application.error;

import com.mlconf.core.domain.common.ErrorCode;

public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public ApplicationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode == null ? ErrorCode.UNKNOWN : errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
