package com.ssafy.spring_boot.security.util;

import com.ssafy.spring_boot.security.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class SecurityUtil {

    /**
     * 현재 인증된 사용자의 ID를 가져옴
     * @return 인증된 사용자 ID (Optional)
     */
    public static Optional<Long> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            log.debug("인증되지 않은 사용자입니다.");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetailsService.CustomUserDetails) {
            CustomUserDetailsService.CustomUserDetails userDetails =
                    (CustomUserDetailsService.CustomUserDetails) principal;
            Long userId = userDetails.getUserId();
            log.debug("현재 인증된 사용자 ID: {}", userId);
            return Optional.of(userId);
        }

        log.warn("예상치 못한 Principal 타입: {}", principal.getClass());
        return Optional.empty();
    }

    /**
     * 현재 인증된 사용자의 이메일을 가져옴
     * @return 인증된 사용자 이메일 (Optional)
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetailsService.CustomUserDetails) {
            CustomUserDetailsService.CustomUserDetails userDetails =
                    (CustomUserDetailsService.CustomUserDetails) principal;
            return Optional.of(userDetails.getUsername()); // 이메일
        }

        return Optional.empty();
    }

    /**
     * 현재 인증된 사용자가 특정 사용자 ID와 일치하는지 확인
     * @param userId 확인할 사용자 ID
     * @return 일치 여부
     */
    public static boolean isCurrentUser(Long userId) {
        Optional<Long> currentUserId = getCurrentUserId();
        boolean isMatch = currentUserId.isPresent() && currentUserId.get().equals(userId);

        if (!isMatch) {
            log.warn("접근 권한 없음. 현재 사용자: {}, 요청 사용자: {}",
                    currentUserId.orElse(null), userId);
        }

        return isMatch;
    }

    /**
     * 현재 인증된 사용자 정보를 가져옴
     * @return CustomUserDetails (Optional)
     */
    public static Optional<CustomUserDetailsService.CustomUserDetails> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetailsService.CustomUserDetails) {
            return Optional.of((CustomUserDetailsService.CustomUserDetails) principal);
        }

        return Optional.empty();
    }
}