package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface RefreshTokenService {
    public RefreshToken createRefreshToken(Long userNo, HttpServletRequest request);
    public Optional<RefreshToken> findByToken(String token);
    public RefreshToken verifyExpiration(RefreshToken token);
    public RefreshToken verifyIpAddress(RefreshToken token, HttpServletRequest request);
    public void deleteByUserNo(Long userNo);
    public void deleteExpiredTokens();
    public void checkSuspiciousActivity(RefreshToken token, HttpServletRequest request);





    }
