package com.example.backend.controller.auth;


import com.example.backend.dto.auth.*;
import com.example.backend.dto.side.SidedatumDto;
import com.example.backend.entity.user.EmailVerificationCode;
import com.example.backend.entity.user.PasswordResetToken;
import com.example.backend.entity.user.User;
import com.example.backend.repository.user.EmailVerificationCodeRepository;
import com.example.backend.repository.user.PasswordResetTokenRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.impl.side.SideServiceImpl;
import com.example.backend.service.impl.user.EmailServiceImpl;
import com.example.backend.utils.security.CodeGenerator;
import com.example.backend.utils.security.JwtUtils;
import com.example.backend.utils.side.DefaultTiptapContentFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationCodeRepository codeRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final CodeGenerator codeGenerator;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final SideServiceImpl sideService;




    @Value("${app.is-production}")
    private boolean isProduction;

    //    登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("登录请求: email={}", loginRequest.email());


        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            // 获取用户信息
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

            // 返回JWT和用户信息
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getEmail(),
                    user.isVipActive()
            ));

        } catch (BadCredentialsException e) {
            log.error("认证失败: email={}, 原因={}", loginRequest.email(), e.getMessage());

            // 检查用户是否存在
            boolean userExists = userRepository.existsByEmail(loginRequest.email());
            log.info("用户存在状态: {}", userExists);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }
    }




    //    验证 Cookie 中的 Token 是否有效
    @GetMapping("/checkSession")
    public ResponseEntity<?> checkSession(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.validateToken(token)) {
                String email = jwtUtils.getEmailFromToken(token);
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

                return ResponseEntity.ok(Map.of(
                        "isAuthenticated", true,
                        "userInfo", Map.of(
                                "id", user.getId(),
                                "username", user.getUsername(),
                                "email", user.getEmail(),
                                "vipActive", user.isVipActive()
                        )
                ));
            }
        }
        return ResponseEntity.ok(Map.of("isAuthenticated", false));
    }



    //退出登录
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 前端负责删除localStorage中的token
        return ResponseEntity.ok("退出登录成功");
    }




    //    注册
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱已被注册");
        }

        EmailVerificationCode codeEntry = validateVerificationCode(
                registerRequest.email(),
                registerRequest.code()
        );

        // 删除已验证的验证码
        codeRepository.delete(codeEntry);


        User newuser = new User();
        newuser.setEmail(registerRequest.email());
        newuser.setUsername(registerRequest.username());
        newuser.setPasswordHash(passwordEncoder.encode(registerRequest.password()));
        newuser.setRole(User.Role.USER);
        newuser.setEmailVerified(false);

        userRepository.save(newuser);


        // 创建默认侧边栏项
        SidedatumDto defaultItem = SidedatumDto.builder()
                .name("欢迎使用指南")
                .href("#")
                .icon("DocumentIcon")
                .tiptapJson(DefaultTiptapContentFactory.createWelcomeDocument())
                .userId(newuser.getId())
                .build();

        sideService.addSideData(defaultItem);

        return ResponseEntity.status(HttpStatus.CREATED).body("注册成功，请检查邮箱完成验证");

    }

    //    重置密码
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request,
            @RequestHeader("Reset-Token") String token
    ) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "未找到有效令牌");
        }

        //    验证令牌
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "无效的令牌"));


        if (resetToken.getExpirationTime().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "令牌已过期");
        }


        //    更新密码
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        //   删除令牌
        passwordResetTokenRepository.delete(resetToken);

//        Cookie cookie = new Cookie("resetToken", null);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(0); // 立即过期
//        response.addCookie(cookie);


        return ResponseEntity.ok("密码重置成功");
    }


    //    发送密码重置的验证码
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ResendRequest request) {
        String email = request.email().trim();

//        检查邮箱是否被注册
        if (!userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "邮箱未注册");
        }


//        生成验证码并发送
        String code = codeGenerator.generate6DigitCode();
        EmailVerificationCode codeEntry = new EmailVerificationCode();
        codeEntry.setCode(code);
        codeEntry.setEmail(email);
        codeEntry.setExpirationTime(Instant.now().plus(5, ChronoUnit.MINUTES));
        codeRepository.save(codeEntry);

        emailServiceImpl.sendPasswordResetEmail(email, code);
        return ResponseEntity.ok("重置验证码已发送至邮箱");
//        return ResponseEntity.ok(Map.of("success", true, "message", "验证码已发送"));
    }

    //    验证重置的验证码并生成令牌
    @PostMapping("/verify-reset-code")
    @Transactional
    public ResponseEntity<?> verifyResetCode(@RequestBody VerifyCodeRequest request,
                                             HttpServletResponse response) {

        EmailVerificationCode codeEntry = validateVerificationCode(
                request.email(),
                request.code()
        );


        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

//        删除令牌
        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();

        // 生成密码重置令牌
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationTime(Instant.now().plus(5, ChronoUnit.MINUTES));
        passwordResetTokenRepository.save(resetToken);

//        清除验证码
        codeRepository.delete(codeEntry);

        // 将令牌写入 HTTP-only Cookie
//        Cookie cookie = new Cookie("resetToken", token);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // 仅在 HTTPS 下发送
//        cookie.setPath("/");
//        cookie.setMaxAge(300);// 设置 Cookie 的过期时间为 5 分钟
//        response.addCookie(cookie);


//        return ResponseEntity.ok(Map.of("token", token));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "resetToken", token
        ));
    }


    //    发送验证码
    @PostMapping("/send-verification")
    @Transactional
    public ResponseEntity<?> sendVerification(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        // 删除该邮箱所有旧验证
        codeRepository.deleteByEmail(email);


        // 检查邮箱是否已注册
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱已被注册");
        }


        // 生成并保存新验证码
        String code = codeGenerator.generate6DigitCode();
        EmailVerificationCode codeEntry = new EmailVerificationCode();
        codeEntry.setCode(code);
        codeEntry.setEmail(email);
        codeEntry.setExpirationTime(Instant.now().plus(5, ChronoUnit.MINUTES));
        codeRepository.save(codeEntry);

        emailServiceImpl.sendVerificationEmail(email, code);
        return ResponseEntity.ok("验证码已发送");
    }

    //    重新发送验证邮件
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody ResendRequest request) {
//        User user = userRepository.findByEmail(request.email())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "邮箱未注册"));

        String email = request.email();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱未注册");
        }

        // 删除旧的验证码
        codeRepository.findByEmail(email).ifPresent(codeRepository::delete);

        // 生成新的6位验证码
        String code = codeGenerator.generate6DigitCode();
        EmailVerificationCode newCodeEntry = new EmailVerificationCode();
        newCodeEntry.setCode(code);
        newCodeEntry.setEmail(email);
        newCodeEntry.setExpirationTime(Instant.now().plus(5, ChronoUnit.MINUTES));
        codeRepository.save(newCodeEntry);

        return ResponseEntity.ok("验证邮件已重新发送");
    }

    //    邮件验证
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyCodeRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "邮箱未注册"));

        EmailVerificationCode codeEntry = codeRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "未找到验证码"));

        // 检查验证码是否匹配
        if (!codeEntry.getCode().equals(request.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误");
        }

        // 检查是否过期
        if (codeEntry.getExpirationTime().isBefore(Instant.now())) {
            codeRepository.delete(codeEntry);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码已过期");
        }

        user.setEmailVerified(true);

        userRepository.save(user);
        codeRepository.delete(codeEntry);


        return ResponseEntity.ok("邮箱验证成功");

    }


    private EmailVerificationCode validateVerificationCode(String email, String userCode) {
        // 获取该邮箱最新的验证码
        List<EmailVerificationCode> codes = codeRepository.findLatestByEmail(email);
        if (codes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "未找到验证码");
        }
        EmailVerificationCode codeEntry = codes.get(0);

        // 检查验证码是否匹配
        if (!codeEntry.getCode().equals(userCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误");
        }

        // 检查验证码是否过期
        if (codeEntry.getExpirationTime().isBefore(Instant.now())) {
            codeRepository.delete(codeEntry);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码已过期");
        }

        return codeEntry;
    }


}
