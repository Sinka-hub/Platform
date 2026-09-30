package com.sinka.platform.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "当前用户资料")
public class UserProfileVO {
    @Schema(description = "用户 ID")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "头像地址，可为空")
    private String avatarUrl;
}
