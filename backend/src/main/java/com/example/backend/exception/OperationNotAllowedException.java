package com.example.backend.exception;

public class OperationNotAllowedException extends RuntimeException {

    // 默认构造函数
    public OperationNotAllowedException(String message) {
        super(message);
    }

    // 构造函数，支持错误信息和异常原因
    public OperationNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
