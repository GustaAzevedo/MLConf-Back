package com.mlconf.core.application.error;

import com.mlconf.core.domain.common.ErrorCode;

public class NotFoundException extends ApplicationException {

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}