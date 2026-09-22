package com.sinka.platform.controller;

import cn.hutool.jwt.JWT;
import com.sinka.platform.constant.JwtConstant;
import com.sinka.platform.entity.Result;
import com.sinka.platform.entity.User;
import com.sinka.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password){
                //登录逻辑
                User user = userService.login(username, password);

                String token = JWT.create()
                        .setKey(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                        .setPayload("userId", user.getId().toString())
                        .setPayload("username", user.getUsername())
                        .setExpiresAt(Date.valueOf(LocalDate.now().plusDays(1)))
                        .sign();
               return Result.success(user);
    }
}
