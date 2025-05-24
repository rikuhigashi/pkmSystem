package com.example.backend.service.impl.user;

import com.example.backend.service.user.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

//    @Override
//    public void sendVerificationEmail(String to, String code) {

    //       //String verificationLink = "http://localhost:5173/verify-email?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("【PKMSystem】邮箱验证码通知");
//        message.setText("感谢您注册！\n\n" +
//                "您的邮箱验证码为：" + code + "\n" +
//                "该验证码5分钟内有效，请尽快完成验证。\n\n" +
//                "如非本人操作，请忽略此邮件。");
//        mailSender.send(message);
//    }
//
//    @Override
//    public void sendPasswordResetEmail(String to, String code) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("【PKMSystem】密码重置验证码");
//        message.setText("您正在重置密码，验证码为：" + code + "\n该验证码5分钟内有效。");
//        mailSender.send(message);
//    }
    @Override
    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("walkurelulu@qq.com"); // 添加此行
        message.setTo(to);
        message.setSubject("【PKMSystem】邮箱验证码通知");
        message.setText("感谢您注册！\n\n您的邮箱验证码为：" + code + "\n该验证码5分钟内有效。");
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("walkurelulu@qq.com"); // 添加此行
        message.setTo(to);
        message.setSubject("【PKMSystem】密码重置验证码");
        message.setText("您正在重置密码，验证码为：" + code + "\n该验证码5分钟内有效。");
        mailSender.send(message);
    }


}
