package com.sinka.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface CoinWalletMapper {

    BigDecimal selectBalanceByUserId(Long userId);

    //创建用户时，创建用户钱包
    int insertWallet(Long userId);

    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    int subtractBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}