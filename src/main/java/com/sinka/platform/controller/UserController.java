package com.sinka.platform.controller;

import cn.hutool.jwt.JWT;
import com.sinka.platform.DTO.LoginRequest;
import com.sinka.platform.DTO.RegistRequest;
import com.sinka.platform.VO.UserProfileVO;
import com.sinka.platform.constant.JwtConstant;
import com.sinka.platform.entity.Result;
import com.sinka.platform.entity.User;
import com.sinka.platform.service.UserService;
import com.sinka.platform.utils.ThreadLocalIdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Tag(name = "用户", description = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户登录", description = "用户名和密码登录，成功后返回 JWT")
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginRequest loginRequest) {
        //登录逻辑
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        //jwtToken生成
        String token = JWT.create()
                .setKey(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .setPayload(JwtConstant.User_ID, user.getId().toString())
                .setPayload(JwtConstant.USER_NAME, user.getUsername())
                .setExpiresAt(Date.from(Instant.now().plusSeconds(JwtConstant.EXPIRE_TIME)))
                .sign();

        return Result.success(token);
    }

    @Operation(summary = "用户注册", description = "创建用户并返回 JWT")
    @PostMapping("/regist")
    public Result regist(@RequestBody RegistRequest registRequest) {
        userService.regist(registRequest);
        User user = userService.login(registRequest.getUsername(), registRequest.getPassword());

        //jwtToken生成
        String token = JWT.create()
                .setKey(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .setPayload(JwtConstant.User_ID, user.getId().toString())
                .setPayload(JwtConstant.USER_NAME, user.getUsername())
                .setExpiresAt(Date.from(Instant.now().plusSeconds(JwtConstant.EXPIRE_TIME)))
                .sign();

        return Result.success(token);
    }

    @Operation(summary = "获取当前用户资料", description = "根据登录信息查询当前用户的公开资料")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public Result<UserProfileVO> getMyProfile() {
        Long userId = ThreadLocalIdUtil.getUserId();
        UserProfileVO profile = userService.getCurrentUser(userId);
        return Result.success(profile);
    }

}
