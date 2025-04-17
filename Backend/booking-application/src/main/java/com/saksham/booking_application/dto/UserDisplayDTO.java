package com.saksham.booking_application.dto;

import java.time.LocalDate;

import com.saksham.booking_application.entity.User;

import lombok.Getter;

@Getter
public class UserDisplayDTO {
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate createdAt;
    private LocalDate deletedAt;

    public UserDisplayDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt().toLocalDate();
        this.deletedAt = user.getDeletedAt() != null ? user.getDeletedAt().toLocalDate() : null;
    }
}
