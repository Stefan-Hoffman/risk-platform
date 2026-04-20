package com.stefan.riskplatform.common.exception;

public class TenantAccessException extends RuntimeException {

    public TenantAccessException(String message) {
        super(message);
    }
}