package com.ssafy.spring_boot.security.jwt;

import com.ssafy.spring_boot.security.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenProvider tokenProvider;  // TokenProvider 추가!
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Request Header에서 JWT 토큰 추출
            String jwt = getJwtFromRequest(request);

            // 2. JWT 토큰이 있고 유효한지 검증
            if (StringUtils.hasText(jwt)) {
                String email = null;

                // 🚀 TokenProvider 토큰 우선 검증 (고급 기능)
                if (tokenProvider.validateAccessToken(jwt)) {
                    email = tokenProvider.getEmailFromToken(jwt);
                    log.debug("TokenProvider로 토큰 검증 성공: {}", email);
                }
                // 기존 JwtUtil 토큰 검증 (호환성)
                else if (jwtUtil.validateToken(jwt)) {
                    email = jwtUtil.getEmailFromToken(jwt);
                    log.debug("JwtUtil로 토큰 검증 성공: {}", email);
                }

                // 3. 이메일이 추출되면 인증 처리
                if (email != null) {
                    // 4. UserDetailsService를 통해 사용자 정보 로드
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // 5. Authentication 객체 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // 6. Request 정보를 Authentication에 추가
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. SecurityContext에 Authentication 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT 인증 성공: {}", email);
                }
            }
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생", e);
            // 인증 실패 시 SecurityContext 초기화
            SecurityContextHolder.clearContext();
        }

        // 8. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 JWT 토큰 추출
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }

        return null;
    }

    /**
     * JWT 인증을 스킵할 경로들 설정 (필요시 오버라이드)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 인증이 필요없는 경로들 (로그인, 회원가입, Swagger 등)
        return path.startsWith("/api/users/signup") ||
                path.startsWith("/api/users/login") ||
                path.startsWith("/api/users/refresh") ||    // 토큰 갱신도 인증 불필요
                path.startsWith("/api/users/legacy-login") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/hello") ||
                path.startsWith("/h2-console");
    }
}