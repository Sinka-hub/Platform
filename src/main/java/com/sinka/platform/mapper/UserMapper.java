package com.sinka.platform.mapper;

import com.sinka.platform.DTO.RegistRequest;
import com.sinka.platform.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectUserByUsername(String username);

    void creatUser(RegistRequest registRequest);
}
