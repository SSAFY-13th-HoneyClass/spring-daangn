package com.ssafy.Daangn.Filter;

import com.ssafy.Daangn.Jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        String jwt = tokenProvider.getAccessToken(request);

        if(StringUtils.hasText(jwt) && tokenProvider.validateAccessToken(jwt)){
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("JWT 토큰 인증 성공: {}", requestURI);

        }else if(StringUtils.hasText(jwt)){
            log.debug("JWT 토큰 만료: {}", requestURI);
        }else{
            log.debug("JWT 토큰 없음: {}", requestURI);
        }

        filterChain.doFilter(request, response);
    }

}
