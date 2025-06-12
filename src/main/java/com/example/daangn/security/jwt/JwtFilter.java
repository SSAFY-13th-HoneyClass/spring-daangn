package com.example.daangn.security.jwt;

import com.example.daangn.domain.user.entity.User;
import com.example.daangn.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader(AUTHORIZATION_HEADER);

        // Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            System.out.println("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.substring(BEARER_PREFIX.length());

        try {
            // 토큰 유효성 검증
            if (!jwtUtil.validateToken(token)) {
                System.out.println("유효하지 않은 토큰입니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰이 만료되었는지 확인
            if (jwtUtil.isExpired(token)) {
                System.out.println("만료된 토큰입니다.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"토큰이 만료되었습니다.\"}");
                return;
            }

            // 토큰에서 username과 role 획득
            String userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            // userEntity를 생성하여 값 set
            User user = new User();
            user.setId(userId);
            user.setPassword("temppassword"); // 실제 패스워드는 필요 없음
            user.setRole("ROLE_USER".equals(role) ? User.Role.USER : User.Role.ADMIN);

            // UserDetails에 회원 정보 객체 담기
            CustomUserDetailsService.CustomUserPrinciple principle =
                    new CustomUserDetailsService.CustomUserPrinciple(user);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principle, null, principle.getAuthorities());

            // 세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("JWT 인증 성공: " + userId + ", Role: " + role);

        } catch (Exception e) {
            System.out.println("JWT 처리 중 오류 발생: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
