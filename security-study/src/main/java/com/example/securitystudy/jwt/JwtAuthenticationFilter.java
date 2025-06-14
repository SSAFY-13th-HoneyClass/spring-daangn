package com.example.securitystudy.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청에서 토큰 추출
        String token = tokenProvider.getAccessToken(request);

        // 토큰이 존재하고 유효하면
        if (token != null && tokenProvider.validateAccessToken(token)) {

            // 토큰으로부터 authentication 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(token);

            // securitycontext 에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
