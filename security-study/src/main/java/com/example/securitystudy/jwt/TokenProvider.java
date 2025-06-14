package com.example.securitystudy.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final UserDetailsService userDetailsService;

    // JWT 서명에 사용할 비밀 키 객체
    private Key key;

    @Value("${spring.jwt.secret}")
    private String secret;

    // AccessToken 만료 시간 설정
    private final long tokenValidityInMilliseconds = 1000 * 60 * 15;

    // 클래스 초기화 직후 실행됨
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 로그인 성공 후 JWT 생성하는 메서드
    public String createAccessToken(Long id, Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        // 유저의 권한 목록을 콤마로 합침 -> JWT의 Claim으로 넣기 위해

        // 현재 시각, 만료 시각 계산
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(id.toString())
                .claim("auth", authorities)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact(); // 문자열로 반환 -> 클라이언트에게 보내는 토큰
    }

    // 요청에서 JWT 추출하는 메서드
    public String getAccessToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        // Authorization: Bearer <token> 헤더에서 토큰만 추출
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    // 토큰 안의 사용자 ID 를 꺼내는 메서드
    public String getTokenUserId(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    // 토큰에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String userId = getTokenUserId(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities()
        );
    }

    // JWT 유효성 검사
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String createRefreshToken(Long id) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (1000L * 60 * 60 * 24 * 7)); // 7일

        return Jwts.builder()
                .setSubject(id.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

}
