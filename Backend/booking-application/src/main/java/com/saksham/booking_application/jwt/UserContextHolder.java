package com.saksham.booking_application.jwt;

import com.saksham.booking_application.dto.CustomUserDetails;

public class UserContextHolder {

    private static final ThreadLocal<CustomUserDetails> userContext = new ThreadLocal<>();

    public static void set(CustomUserDetails userDetails) {
        userContext.set(userDetails);
    }

    public static CustomUserDetails get() {
        return userContext.get();
    }

    public static void clear() {
        userContext.remove();
    }
}