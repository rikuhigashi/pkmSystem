package com.example.backend.exception;
import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends RuntimeException {

    private final int errorCode;
    private final HttpStatus httpStatus;

    // 基础构造函数，传递错误消息
    public AuthenticationFailedException(String message) {
        super(message);
        this.errorCode = 1001;  // 默认错误码
        this.httpStatus = HttpStatus.UNAUTHORIZED;  // 默认 HTTP 状态码
    }

    // 带有错误码的构造函数
    public AuthenticationFailedException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.UNAUTHORIZED;  // 默认 HTTP 状态码
    }

    // 带有错误码和 HTTP 状态码的构造函数
    public AuthenticationFailedException(String message, int errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    // 带有错误消息和嵌套异常的构造函数
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 1001;  // 默认错误码
        this.httpStatus = HttpStatus.UNAUTHORIZED;  // 默认 HTTP 状态码
    }

    // 获取错误码
    public int getErrorCode() {
        return errorCode;
    }

    // 获取 HTTP 状态码
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
