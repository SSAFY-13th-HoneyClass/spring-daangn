package com.ssafy.daangn.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final UserDetailsService userDetailsService;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            UserDetailsService userDetailsService) {
        this.secret = secret;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7); // "Bearer " 제거
        }

        return null;
    }

    public String createAccessToken(Long userNo, Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userNo.toString())
                .claim(AUTHORITIES_KEY, authorities)
                .claim("tokenType", "ACCESS") // 토큰 타입 구분
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getTokenUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getTokenUserId(token));
        return new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities());
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // ACCESS 토큰인지 확인
            String tokenType = claims.get("tokenType", String.class);
            if (!"ACCESS".equals(tokenType)) {
                log.info("ACCESS 토큰이 아닙니다.");
                return false;
            }

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 토큰에서 만료 시간 추출
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}


//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import jakarta.servlet.http.HttpServletRequest;
//import java.security.Key;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//public class TokenProvider implements InitializingBean {
//
//    private static final String AUTHORITIES_KEY = "auth";
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String BEARER_TYPE = "Bearer";
//
//    private final String secret;
//    private final long tokenValidityInMilliseconds;
//    private final UserDetailsService userDetailsService;
//
//    private Key key;
//
//    public TokenProvider(
//            @Value("${jwt.secret}") String secret,
//            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
//            UserDetailsService userDetailsService) {
//        this.secret = secret;
//        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//        byte[] keyBytes = Decoders.BASE64.decode(secret);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public String getAccessToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
//            return bearerToken.substring(7); // "Bearer " 제거
//        }
//
//        return null;
//    }
//
//    public String createAccessToken(Long userNo, Authentication authentication) {
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        long now = (new Date()).getTime();
//        Date validity = new Date(now + this.tokenValidityInMilliseconds);
//
//        return Jwts.builder()
//                .setSubject(userNo.toString()) // 사용자 번호를 subject로 설정
//                .claim(AUTHORITIES_KEY, authorities)
//                .setIssuedAt(new Date(now))
//                .setExpiration(validity)
//                .signWith(key, SignatureAlgorithm.HS512)
//                .compact();
//    }
//
//    public String getTokenUserId(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject(); // userNo가 String으로 저장됨
//    }
//
//    public Authentication getAuthentication(String token) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(getTokenUserId(token));
//        return new UsernamePasswordAuthenticationToken(
//                userDetails, token, userDetails.getAuthorities());
//    }
//
//    public boolean validateAccessToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
//            log.info("잘못된 JWT 서명입니다.");
//        } catch (ExpiredJwtException e) {
//            log.info("만료된 JWT 토큰입니다.");
//        } catch (UnsupportedJwtException e) {
//            log.info("지원되지 않는 JWT 토큰입니다.");
//        } catch (IllegalArgumentException e) {
//            log.info("JWT 토큰이 잘못되었습니다.");
//        }
//        return false;
//    }
//}