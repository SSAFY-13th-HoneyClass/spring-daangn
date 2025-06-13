package com.example.daangn.security.jwt;

import com.example.daangn.domain.user.entity.User;
import com.example.daangn.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 인증이 필요하지 않은 경로는 바로 통과
        String requestURI = request.getRequestURI();
        if (isPublicPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 토큰 추출
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        // Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            log.debug("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.substring(BEARER_PREFIX.length());

        try {
            // 토큰 유효성 검증
            if (!jwtUtil.validateToken(token)) {
                log.debug("유효하지 않은 토큰입니다: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰이 만료되었는지 확인
            if (jwtUtil.isExpired(token)) {
                log.debug("만료된 토큰입니다: {}", requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"토큰이 만료되었습니다.\", \"code\": \"TOKEN_EXPIRED\"}");
                return;
            }

            // Access Token인지 확인
            if (!jwtUtil.isAccessToken(token)) {
                log.debug("Access Token이 아닙니다: {}", requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"유효하지 않은 토큰 타입입니다.\", \"code\": \"INVALID_TOKEN_TYPE\"}");
                return;
            }

            // 토큰에서 사용자 정보 추출
            String userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            // User 엔티티 생성 (실제 DB 조회 대신 토큰 정보 사용)
            User user = new User();
            user.setId(userId);
            user.setPassword(""); // 패스워드는 필요 없음
            user.setRole("ROLE_ADMIN".equals(role) ? User.Role.ADMIN : User.Role.USER);

            // UserDetails 생성
            CustomUserDetailsService.CustomUserPrinciple principal =
                    new CustomUserDetailsService.CustomUserPrinciple(user);

            // Spring Security 인증 토큰 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());

            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT 인증 성공: {} ({})", userId, role);

        } catch (Exception e) {
            log.error("JWT 처리 중 오류 발생: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"토큰 처리 중 오류가 발생했습니다.\", \"code\": \"TOKEN_PROCESSING_ERROR\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 인증이 필요하지 않은 공개 경로인지 확인
     */
    private boolean isPublicPath(String requestURI) {
        return requestURI.equals("/") ||
                requestURI.startsWith("/auth/") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs");
    }
}


