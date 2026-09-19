package com.flowforge.backend.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.flowforge.backend.auth.entity.User;

public class RegisterResponse {

    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public RegisterResponse(
            UUID id,
            String name,
            String email,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static RegisterResponse fromUser(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}