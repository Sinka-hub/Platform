package com.sinka.platform.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginRequest {

    @Schema(description = "用户名", example = "test")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", example = "123")
    @NotBlank(message = "密码不能为空")
    private String password;
}
