package com.aynur.profile_service.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CurrentUserProvider {
    public Long getCurrentUserId() {
        var request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes())
                .getRequest();
        String userId = request.getHeader("X-User-Id");
        return Long.parseLong(userId);
    }
}