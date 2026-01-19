package com.mlconf.core.domain.common;

public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    public DomainException(String message) {
        this(ErrorCode.DOMAIN_RULE_VIOLATION, message, null);
    }

    public DomainException(String message, Throwable cause) {
        this(ErrorCode.DOMAIN_RULE_VIOLATION, message, cause);
    }

    public DomainException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public DomainException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode == null ? ErrorCode.UNKNOWN : errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
