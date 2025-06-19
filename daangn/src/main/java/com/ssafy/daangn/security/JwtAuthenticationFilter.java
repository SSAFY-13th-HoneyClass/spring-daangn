package com.ssafy.daangn.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 JWT 토큰 추출
        String jwt = tokenProvider.getAccessToken(request);

        // 2. 토큰이 유효한지 검증
        if (StringUtils.hasText(jwt) && tokenProvider.validateAccessToken(jwt)) {
            try {
                // 3. 토큰에서 Authentication 객체 생성
                Authentication authentication = tokenProvider.getAuthentication(jwt);

                // 4. SecurityContext에 Authentication 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.",
                        authentication.getName());
            } catch (Exception e) {
                log.error("Authentication 설정 중 오류 발생: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("유효한 JWT 토큰이 없습니다.");
        }

        filterChain.doFilter(request, response);
    }
}
