package com.suvaathy.hrmanagement.exception;

public abstract class HrBusinessException extends RuntimeException {
    public HrBusinessException(String message) { super(message); }
    public HrBusinessException(String message, Throwable cause) { super(message, cause); }
}