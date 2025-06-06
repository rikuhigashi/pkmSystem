package com.example.backend.exception;

public class UnauthorizedException extends RuntimeException {

    // 构造函数，接收错误消息
    public UnauthorizedException(String message) {
        super(message);  // 调用父类构造函数，传递错误消息
    }


    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
