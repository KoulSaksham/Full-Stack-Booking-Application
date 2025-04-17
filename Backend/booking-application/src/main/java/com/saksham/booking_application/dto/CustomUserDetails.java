package com.saksham.booking_application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails {
    private String token;
    private String username;
    private String email;
    private boolean isAdmin = false;
}
