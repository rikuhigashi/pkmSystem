//package com.example.backend.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(false); // 允许携带 Cookie
////        config.setAllowedOrigins(List.of("http://localhost:5173")); // 允许的前端地址
//        config.setAllowedOrigins(Arrays.asList(
//                "https://*.ngrok-free.app",
//                "https://ecd2-171-39-22-163.ngrok-free.app",
//                "http://localhost:5173"
//
//        ));
//        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH")); // 允许的请求方法
//        config.setAllowedHeaders(List.of("*")); // 允许的请求头
//        config.setExposedHeaders(List.of("Authorization")); // 允许前端读取的响应头
////        config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        source.registerCorsConfiguration("/api/payment/status/**", config);
//        return new CorsFilter(source);
//    }
//
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
//        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(corsFilter());
//        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return registration;
//    }
//
//}
