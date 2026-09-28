package com.sinka.platform.VO;

import lombok.Data;

@Data
public class UserProfileVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
}