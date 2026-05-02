package io.github.caiohbs.authentication.util;

import io.github.caiohbs.authentication.model.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestContextUtil {

    private RequestContextUtil() {
    }

    public static String getClientIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    public static String getUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getHeader("User-Agent") : null;
    }

    public static String getAcceptLanguage() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getHeader("Accept-Language") : null;
    }

    public static String getReferer() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getHeader("Referer") : null;
    }

    public static String getDeviceType() {
        String userAgent = getUserAgent();
        if (userAgent == null) {
            return "UNKNOWN";
        }

        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android")) {
            return "MOBILE";
        }
        if (ua.contains("tablet") || ua.contains("ipad")) {
            return "TABLET";
        }
        return "DESKTOP";
    }

    public static String getOperatingSystem() {
        String ua = getUserAgent();
        if (ua == null) {
            return "NOT FOUND";
        }

        String userAgent = ua.toLowerCase();

        if (userAgent.contains("windows")) {
            return "WINDOWS";
        }
        if (userAgent.contains("macintosh") || userAgent.contains("mac os")) {
            return "MACOS";
        }
        if (userAgent.contains("linux") && !userAgent.contains("android")) {
            return "LINUX";
        }
        if (userAgent.contains("android")) {
            return "ANDROID";
        }
        if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "IOS";
        }
        return "UNKNOWN";
    }

    public static String getBrowser() {
        String userAgent = getUserAgent();
        if (userAgent == null) {
            return "NOT FOUND";
        }

        if (userAgent.contains("Edg")) {
            return "EDGE";
        }
        if (userAgent.contains("Chrome")) {
            return "CHROME";
        }
        if (userAgent.contains("Safari")) {
            return "SAFARI";
        }
        if (userAgent.contains("Firefox")) {
            return "FIREFOX";
        }
        if (userAgent.contains("Opera") || userAgent.contains("OPR")) {
            return "OPERA";
        }
        return "UNKNOWN";
    }

    public static String getRequestUri() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getRequestURI() : null;
    }

    public static String getMethod() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getMethod() : null;
    }

    public static String getHeader(String headerName) {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getHeader(headerName) : null;
    }

    private static HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) attributes).getRequest();
        }
        return null;
    }

    public static RequestContext getRequestContent() {
        return new RequestContext(
                RequestContextUtil.getUserAgent(),
                RequestContextUtil.getDeviceType(),
                RequestContextUtil.getOperatingSystem()
        );
    }

}
