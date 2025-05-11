package com.example.backend.utils.security;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CodeGenerator {
    public String generate6DigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000-999999之间的数字
        return String.valueOf(code);
    }
}
