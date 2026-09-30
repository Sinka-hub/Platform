package com.sinka.platform.service;

import com.sinka.platform.DTO.CoinRechargeRequest;
import com.sinka.platform.VO.CoinTransactionVO;
import com.sinka.platform.VO.PageResultVO;

import java.math.BigDecimal;

public interface CoinWalletService {

    BigDecimal getBalance();

    BigDecimal recharge(CoinRechargeRequest request);

    PageResultVO<CoinTransactionVO> getTransactions(Integer pageNum, Integer pageSize);
}