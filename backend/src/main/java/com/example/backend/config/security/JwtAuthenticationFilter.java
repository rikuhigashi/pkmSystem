package com.example.backend.config.security;

import com.example.backend.utils.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        String token = parseToken(request);

        if (token != null && jwtUtils.validateToken(token)) {
//            String username = jwtUtils.getUsernameFromToken(token);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String username = jwtUtils.getEmailFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);

    }


    private String parseToken(HttpServletRequest request) {

//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("authToken".equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }

        String header = request.getHeader("Authorization");

        if (header != null) {
            // 处理Bearer token格式
            String[] parts = header.trim().split("\\s+");
            if (parts.length == 2 && "Bearer".equalsIgnoreCase(parts[0])) {
                return parts[1].trim();
            }
        }

//        if (header != null && header.startsWith("Bearer ")) {
//            String token = header.substring(7).trim();
//            log.debug("原始 Token: {}", header.substring(7));
//            log.debug("处理后的 Token: {}", token);
//            return token.isEmpty() ? null : token;
//        }
        return null;
    }

}
