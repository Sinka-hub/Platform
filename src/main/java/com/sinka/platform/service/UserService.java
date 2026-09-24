package com.sinka.platform.service;

import com.sinka.platform.DTO.RegistRequest;
import com.sinka.platform.entity.User;

public interface UserService {

    User login(String username, String password);

    void regist(RegistRequest registRequest);
}
