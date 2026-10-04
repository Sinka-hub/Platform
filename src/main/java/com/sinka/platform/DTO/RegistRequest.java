package com.sinka.platform.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "注册请求")
public class RegistRequest {
    @Schema(description = "用户名", example = "demo_user")
    @NotBlank(message = "用户名不能为空")
    private String username;
    @Schema(description = "密码", example = "demo_password")
    @NotBlank(message = "密码不能为空")
    private String password;
    @Schema(description = "昵称", example = "示例用户")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

}
