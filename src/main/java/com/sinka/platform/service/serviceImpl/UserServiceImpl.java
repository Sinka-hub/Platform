package com.sinka.platform.service.serviceImpl;

import cn.hutool.json.ObjectMapper;
import com.sinka.platform.DTO.RegistRequest;
import com.sinka.platform.VO.UserProfileVO;
import com.sinka.platform.entity.User;
import com.sinka.platform.mapper.CoinWalletMapper;
import com.sinka.platform.mapper.UserMapper;
import com.sinka.platform.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CoinWalletMapper coinWalletMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectUserByUsername(username);
        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(password)) {
            return null;
        }
        user.setPassword("********");
        return user;
    }

    @Override
    @Transactional
    public void regist(RegistRequest registRequest) {
        User user = new User();
        BeanUtils.copyProperties(registRequest, user);

        userMapper.creatUser(user);
        coinWalletMapper.insertWallet(user.getId());
    }

    @Override
    public UserProfileVO getCurrentUser(Long userId) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return null;
        }
        UserProfileVO userVO = new UserProfileVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }
}
