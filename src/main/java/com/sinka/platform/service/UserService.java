package com.sinka.platform.service;

import com.sinka.platform.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User login(String username, String password);
}
