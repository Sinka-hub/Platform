package com.sinka.platform.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sinka.platform.DTO.CoinRechargeRequest;
import com.sinka.platform.VO.CoinTransactionVO;
import com.sinka.platform.VO.PageResultVO;
import com.sinka.platform.mapper.CoinTransactionMapper;
import com.sinka.platform.mapper.CoinWalletMapper;
import com.sinka.platform.service.CoinWalletService;
import com.sinka.platform.utils.ThreadLocalIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CoinWalletServiceImpl implements CoinWalletService {

    @Autowired
    private CoinWalletMapper coinWalletMapper;

    @Autowired
    private CoinTransactionMapper coinTransactionMapper;

    @Override
    public BigDecimal getBalance() {
        Long userId = ThreadLocalIdUtil.getUserId();
        return coinWalletMapper.selectBalanceByUserId(userId);
    }

    @Override
    @Transactional
    public BigDecimal recharge(CoinRechargeRequest request) {
        Long userId = ThreadLocalIdUtil.getUserId();
        BigDecimal amount = request.getAmount();

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }

        // 验证并规范化 UUID
        String requestId = UUID.fromString(request.getRequestId()).toString();
        String bizNo = "RECHARGE:" + requestId;

        // 先记录流水，唯一业务编号会阻止重复充值
        coinTransactionMapper.insertTransaction(
                userId, null, "RECHARGE", amount, bizNo);

        // 增加余额
        if (coinWalletMapper.addBalance(userId, amount) != 1) {
            throw new IllegalStateException("钱包不存在");
        }

        return coinWalletMapper.selectBalanceByUserId(userId);
    }

    @Override
    public PageResultVO<CoinTransactionVO> getTransactions(
            Integer pageNum, Integer pageSize) {
        Long userId = ThreadLocalIdUtil.getUserId();

        PageHelper.startPage(pageNum, pageSize);
        Page<CoinTransactionVO> transactions =
                (Page<CoinTransactionVO>)
                        coinTransactionMapper.selectByUserId(userId);

        PageResultVO<CoinTransactionVO> result = new PageResultVO<>();
        result.setRecords(transactions.getResult());
        result.setPages(transactions.getPages());
        return result;
    }
}