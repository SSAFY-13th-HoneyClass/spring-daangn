package com.example.daangn.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}")String secret) {


        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    /**
     * 토큰으로부터 유저 id 추출
     * */
    public String getUserId(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey) // 키 확인
                .build()
                .parseSignedClaims(token) // 클레임 확인
                .getPayload() // 데이터 추출
                .get("username", String.class); // id 추출
    }

    /**
     * 토큰으로부터 유저 role 추출
     * */
    public String getRole(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey) // 키 확인
                .build()
                .parseSignedClaims(token) // 클레임 확인
                .getPayload() // 데이터 추출
                .get("role", String.class); //역할 추출
    }

    /**
     * 토큰 만료 여부 확인
     * */
    public Boolean isExpired(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public String createToken(Authentication authentication, Long expiredMs) {
        return Jwts.builder()
                .claim("authentication", authentication)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
