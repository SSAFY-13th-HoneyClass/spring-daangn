package com.ssafy.springdaangn.config;

import com.ssafy.springdaangn.jwt.JwtAuthenticationFilter;
import com.ssafy.springdaangn.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    // 비밀번호 암호화에 사용할 Bean 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring Security 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(auth -> auth.disable())
                // form 로그인 비활성화
                .formLogin(auth -> auth.disable())
                // HTTP Basic 인증 비활성화
                .httpBasic(auth -> auth.disable())
                // 요청별 인증/인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**").authenticated()
                                .anyRequest().permitAll()
//                        .requestMatchers("/login", "/", "/join").permitAll()   // 누구나 접근 가능
//                        .requestMatchers("/admin/**").hasRole("ADMIN")         // ADMIN 권한 필요
//                        .requestMatchers("/swagger-ui/**").permitAll()
//                        .anyRequest().authenticated()                          // 그 외는 인증 필요
               )
                //api 호출 전 실행될 필터
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }
}
