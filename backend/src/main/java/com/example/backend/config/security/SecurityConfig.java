package com.example.backend.config.security;

import com.example.backend.utils.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;


    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint, JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.authEntryPoint = authEntryPoint;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF和CORS默认配置
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 异常处理配置
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPoint)  // 自定义认证入口
                )
                // 会话管理
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 无状态会话
                )
                // 请求权限配置
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()  // 允许匿名访问登录接口
                                .requestMatchers("/upload/**").authenticated()
                                .requestMatchers(
                                        "/api/payment/alipay/notify",
                                        "/api/payment/alipay/return"
                                ).permitAll()
//                        .requestMatchers(HttpMethod.POST, "/sideData/upload-image").authenticated()
                                .requestMatchers("/tags/**").hasAnyRole("ADMIN", "USER") // 仅允许ADMIN和USER角色访问标签相关接口
                                .requestMatchers("/api/tiptap/**").authenticated()
                                .requestMatchers("/admin/**").hasRole("ADMIN")// 仅允许ADMIN角色访问
                                .requestMatchers("/api/payment/**").authenticated()

                                .anyRequest().authenticated()  // 其他请求需认证
                )
                // 添加JWT过滤器
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);  // 添加JWT过滤器


        return http.build();
    }

    // 认证管理器
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 密码编码器
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 配置
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:5173")); // 前端地址
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://pkm-system.vercel.app",
                "https://pkm-system-higashis-projects-6c54d254.vercel.app/"
        )); // 前端地址
//        config.setAllowedMethods(List.of("*"));
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS",
                "HEAD", "PATCH", "TRACE", "CONNECT",
                "COPY", "MOVE", "LINK", "UNLINK"
        ));
        config.setAllowCredentials(true); // 允许携带 Cookie
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Reset-Token"));
        config.setExposedHeaders(List.of("Authorization", "Content-Length"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
