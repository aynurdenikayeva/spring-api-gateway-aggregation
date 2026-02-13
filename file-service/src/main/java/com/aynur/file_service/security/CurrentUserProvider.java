package com.aynur.file_service.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Sadə variant: userId-ni header-dən oxuyuruq.
 * Məs: X-User-Id: 15
 * (JWT varsa sonra SecurityContext-dən götürərik)
 */
@Component
public class CurrentUserProvider {
    public Long getCurrentUserIdOrNull() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null || attrs.getRequest() == null) return null;

        String val = attrs.getRequest().getHeader("X-User-Id");
        if (val == null || val.isBlank()) return null;
        try {
            return Long.parseLong(val);
        } catch (Exception e) {
            return null;
        }
    }
}