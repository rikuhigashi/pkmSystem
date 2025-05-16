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
import com.example.backend.service.side.SideService;
import com.example.backend.utils.security.CodeGenerator;
import com.example.backend.utils.security.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailVerificationCodeRepository codeRepository, EmailServiceImpl emailServiceImpl, CodeGenerator codeGenerator, PasswordResetTokenRepository passwordResetTokenRepository, SideServiceImpl sideService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.codeRepository = codeRepository;
        this.emailServiceImpl = emailServiceImpl;
        this.codeGenerator = codeGenerator;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.sideService = sideService;
    }

    //    登录
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        try {
            // 使用邮箱进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成 JWT
            String jwt = jwtUtils.generateJwtToken(authentication);

            Cookie cookie = new Cookie("authToken", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // 生产环境启用
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);// 设置 Cookie 的过期时间为 1 小时
            response.addCookie(cookie);


            // 从 UserDetails 获取邮箱，然后查询数据库获取用户名
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername(); // UserDetails 的 username 是邮箱
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

//            return ResponseEntity.ok(new JwtResponse(jwt, user.getUsername()) + "登录成功");
            return ResponseEntity.ok(new JwtResponse(jwt, user.getUsername(),user.getRole()));


        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }
    }

//    验证 Cookie 中的 Token 是否有效
   @GetMapping("/checkSession")
    public ResponseEntity<?> checkSession(@CookieValue(name = "authToken", required = false) String token) {
        // 检查用户是否已登录
       if (token != null && jwtUtils.validateToken(token)) {

           // 从 JWT 中提取邮箱
           String email = jwtUtils.getEmailFromToken(token);
           User user = userRepository.findByEmail(email)
                   .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

           // 返回用户信息
           return ResponseEntity.ok(Map.of(
                   "isAuthenticated", true,
                   "userInfo", Map.of("username", user.getUsername())
           ));

       }
    return ResponseEntity.ok(Map.of("isAuthenticated", false));}


    //退出登录
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 立即过期
        response.addCookie(cookie);

        return ResponseEntity.ok("退出登录成功");
    }


    //    注册
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱已被注册");
        }





        User newuser = new User();
        newuser.setEmail(registerRequest.email());
        newuser.setUsername(registerRequest.username());
        newuser.setPasswordHash(passwordEncoder.encode(registerRequest.password()));
        newuser.setRole(User.Role.USER);
        newuser.setEmailVerified(false);

        userRepository.save(newuser);


        // 创建默认侧边栏项
        SidedatumDto defaultItem = SidedatumDto.builder()
                .name("我的第一个文档")
                .href("#")
                .icon("DocumentIcon")
                .tiptapJson(Map.of(
                        "type", "doc",
                        "content", List.of(Map.of(
                                "type", "heading",
                                "attrs", Map.of("level", 2),
                                "content", List.of(Map.of("type", "text", "text", "欢迎使用"))
                        ))
                ))
                .userId(newuser.getId())
                .build();

        sideService.addSideData(defaultItem);


//        String token = UUID.randomUUID().toString();
//        EmailVerificationToken verificationToken = new EmailVerificationToken();
//       verificationToken.setToken(token);
//        verificationToken.setUser(newuser);
//        verificationToken.setExpirationTime(Instant.now().plus(5, ChronoUnit.MINUTES));
//
//        tokenRepository.save(verificationToken);


        return ResponseEntity.status(HttpStatus.CREATED).body("注册成功，请检查邮箱完成验证");

    }

    //    重置密码
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request,
            @CookieValue(name = "resetToken", required = false) String token) {
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

        return ResponseEntity.ok("密码重置成功");
    }


    //    发送密码重置的验证码
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ResendRequest request) {
        String email = request.email();

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

    }

    //    验证重置的验证码并生成令牌
    @PostMapping("/verify-reset-code")
    @Transactional
    public ResponseEntity<?> verifyResetCode(@RequestBody VerifyCodeRequest request,
                                             HttpServletResponse response) {
        // 获取该邮箱所有验证码（按过期时间倒序）
        List<EmailVerificationCode> codes = codeRepository.findLatestByEmail(request.email());
        if (codes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "未找到验证码");
        }

        // 取最新的一条
        EmailVerificationCode codeEntry = codes.get(0);

        //检查验证码是否匹配
        if (!codeEntry.getCode().equals(request.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误");
        }

//        检查是否过期
        if (codeEntry.getExpirationTime().isBefore(Instant.now())) {
            codeRepository.delete(codeEntry);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码已过期");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

//        删除令牌
        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();

        // 生成密码重置令牌
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
//        resetToken.setUser(userRepository.findByEmail(request.email()).get());
        resetToken.setUser(user);
        resetToken.setExpirationTime(Instant.now().plus(5, ChronoUnit.MINUTES));
        passwordResetTokenRepository.save(resetToken);

//        清除验证码
        codeRepository.delete(codeEntry);

        // 将令牌写入 HTTP-only Cookie
        Cookie cookie = new Cookie("resetToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // 仅在 HTTPS 下发送
        cookie.setPath("/");
        cookie.setMaxAge(300);// 设置 Cookie 的过期时间为 5 分钟
        response.addCookie(cookie);


//        return ResponseEntity.ok(Map.of("token", token));
        return ResponseEntity.ok().build();
    }


    //    发送验证码
    @PostMapping("/send-verification")
    @Transactional
    public ResponseEntity<?> sendVerification(@RequestBody Map<String, String> request) {

        String email = request.get("email");


        codeRepository.deleteByEmail(email);


        // 检查邮箱是否已注册
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱已被注册");
        }

        // 删除该邮箱所有未过期的旧验证码
        codeRepository.deleteByEmail(email);

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


}
