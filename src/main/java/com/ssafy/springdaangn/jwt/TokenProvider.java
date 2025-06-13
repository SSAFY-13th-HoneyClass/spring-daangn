package com.ssafy.springdaangn.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider implements InitializingBean {
    private static final long expirationTime=3600*1000;
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private Key key;
    private final UserDetailsService userDetailsService;

    public TokenProvider(@Value("${jwt.secret}") String secret, @Lazy UserDetailsService userDetailsService) {
        this.secret = secret;
        this.userDetailsService=userDetailsService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    public String createToken(Long id, Authentication authentication) { //토큰 발급
        // 사용자 권한 목록을 가져오자 ( authorities = "ROLE_USER" )
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Token 만료 시간 설정 ( expirationTime = 30분으로 설정 )
        long now = (new Date().getTime());

        Date tokenExpiresIn = new Date(now + expirationTime);

        // JJWT 라이브러리의 JWT 생성 빌더 패턴 사용
        return Jwts.builder()
                .setSubject(id.toString())           // subject : JWT 토큰이 누구인지 식별
                .claim(AUTHORITIES_KEY, authorities) // claim : 사용자 권한 정보
                .setExpiration(tokenExpiresIn)       // expiration : 만료 시간
                .signWith(key, SignatureAlgorithm.HS512)  // signwith : 토큰 서명 ( 비밀키 )
                .compact(); // 최종 JWT 문자열 생성
    }
    public boolean validateAccessToken(String token) { //토큰 로직 검증
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
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

    public String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch(Exception e){
            return null;
        }
    }


    public Long getAccessTokenExpirationTime() {
        return expirationTime;
    }
}
