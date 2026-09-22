package com.sinka.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class User {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatarUrl;

    private String accountStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
