package com.example.daangn.security.jwt;

import com.example.daangn.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

        // 파라미터 이름 설정 (주의!! 이걸로 1시간 삽질함)

        // 로그인 처리 URL을 /auth/login으로 설정
        // UsernamePasswordAuthenticationFilter는 기본적으로 /login 경로에서만 동작하기 때문
        setFilterProcessesUrl("/auth/login");
        // UsernamePasswordAuthenticationFilter는 기본적으로 username으로 값을 받고 저장
        setUsernameParameter("id");        // 'id' 파라미터를 username으로 사용
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 username, password 추출
        String userId = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println("loginfilter: " + userId);

        //스프링 시큐리티에서 username과 password를 검증하기 위해 token에 담기
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, password);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetailsService.CustomUserPrinciple principal = (CustomUserDetailsService.CustomUserPrinciple) authentication.getPrincipal();

        String userId = principal.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 10시간 = 10 * 60 * 60 * 1000 밀리초
        Long expiredMs = 10 * 60 * 60 * 1000L;

        // 사용자 정보와 만료 시간을 넘겨주며 토큰 생성
        String token = jwtUtil.createToken(userId, role, expiredMs);

        // 헤더에 토큰 추가
        response.addHeader("Authorization", "Bearer " + token);

        System.out.println("successfulAuthentication: " + token);
        System.out.println("Token expires in: " + expiredMs + " milliseconds");
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        // 로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
        System.out.println("Login failed: " + failed.getMessage());
    }
}
