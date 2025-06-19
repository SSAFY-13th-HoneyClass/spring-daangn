package com.ssafy.spring_boot.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ID_KEY = "userId";
    private static final String TOKEN_TYPE_KEY = "tokenType";

    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenValidityInMilliseconds) {

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;

        log.info("TokenProvider 초기화 완료 - Access Token 만료시간: {}ms, Refresh Token 만료시간: {}ms",
                accessTokenValidityInMilliseconds, refreshTokenValidityInMilliseconds);
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long userId, String email, String authorities) {
        return createToken(userId, email, authorities, "ACCESS", accessTokenValidityInMilliseconds);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Long userId, String email, String authorities) {
        return createToken(userId, email, authorities, "REFRESH", refreshTokenValidityInMilliseconds);
    }

    /**
     * JWT 토큰 생성 (공통)
     */
    private String createToken(Long userId, String email, String authorities, String tokenType, long validityPeriod) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityPeriod);

        return Jwts.builder()
                .subject(email)
                .claim(USER_ID_KEY, userId)
                .claim(AUTHORITIES_KEY, authorities)
                .claim(TOKEN_TYPE_KEY, tokenType)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 Authentication 객체 생성
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get(USER_ID_KEY, Long.class);
    }

    /**
     * JWT 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * JWT 토큰에서 권한 정보 추출
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get(AUTHORITIES_KEY, String.class);
    }

    /**
     * JWT 토큰 타입 확인 (ACCESS/REFRESH)
     */
    public String getTokenType(String token) {
        Claims claims = parseClaims(token);
        return claims.get(TOKEN_TYPE_KEY, String.class);
    }

    /**
     * JWT 토큰이 Access Token인지 확인
     */
    public boolean isAccessToken(String token) {
        return "ACCESS".equals(getTokenType(token));
    }

    /**
     * JWT 토큰이 Refresh Token인지 확인
     */
    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(getTokenType(token));
    }

    /**
     * JWT 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * Access Token 전용 유효성 검증
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token) && isAccessToken(token);
    }

    /**
     * Refresh Token 전용 유효성 검증
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token) && isRefreshToken(token);
    }

    /**
     * JWT 토큰 만료 시간 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * JWT 토큰의 남은 유효 시간 (밀리초)
     */
    public long getRemainingTime(String token) {
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * JWT Claims 파싱
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Access Token 만료시간 getter
     */
    public long getAccessTokenValidityInMilliseconds() {
        return accessTokenValidityInMilliseconds;
    }

    /**
     * Refresh Token 만료시간 getter
     */
    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInMilliseconds;
    }

    /**
     * 토큰 정보 요약 (디버깅용)
     */
    public String getTokenInfo(String token) {
        try {
            Claims claims = parseClaims(token);
            return String.format("토큰 정보 - 사용자: %s, ID: %s, 타입: %s, 만료: %s",
                    claims.getSubject(),
                    claims.get(USER_ID_KEY),
                    claims.get(TOKEN_TYPE_KEY),
                    claims.getExpiration()
            );
        } catch (Exception e) {
            return "유효하지 않은 토큰";
        }
    }
}