package com.ssafy.daangn.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class IpUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (StringUtils.hasText(ipList) && !"unknown".equalsIgnoreCase(ipList)) {
                // X-Forwarded-For는 여러 IP가 콤마로 구분될 수 있음
                String ip = ipList.split(",")[0].trim();
                if (isValidIpAddress(ip)) {
                    return ip;
                }
            }
        }

        return request.getRemoteAddr();
    }

    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String userAgent = request.getHeader("User-Agent");
        return StringUtils.hasText(userAgent) ? userAgent : "unknown";
    }

    private static boolean isValidIpAddress(String ip) {
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }

        // 간단한 IP 주소 형식 검증
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // IP 주소 마스킹 (로그용)
    public static String maskIpAddress(String ip) {
        if (!StringUtils.hasText(ip)) {
            return "unknown";
        }

        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".***." + parts[3];
        }

        return "masked";
    }
}