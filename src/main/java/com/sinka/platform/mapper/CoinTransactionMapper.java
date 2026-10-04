package com.sinka.platform.mapper;

import com.sinka.platform.VO.CoinTransactionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CoinTransactionMapper {

    int insertTransaction(
            @Param("userId") Long userId,
            @Param("orderId") Long orderId,
            @Param("type") String type,
            @Param("amount") BigDecimal amount,
            @Param("bizNo") String bizNo);

    List<CoinTransactionVO> selectByUserId(
            @Param("userId") Long userId);
}

