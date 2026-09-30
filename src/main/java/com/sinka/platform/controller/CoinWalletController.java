package com.sinka.platform.controller;

import com.sinka.platform.DTO.CoinRechargeRequest;
import com.sinka.platform.VO.CoinTransactionVO;
import com.sinka.platform.VO.PageResultVO;
import com.sinka.platform.entity.Result;
import com.sinka.platform.service.CoinWalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
@SecurityRequirement(name = "bearerAuth")
public class CoinWalletController {

    @Autowired
    private CoinWalletService coinWalletService;

    @GetMapping
    public Result<BigDecimal> getBalance() {
        return Result.success(coinWalletService.getBalance());
    }

    @PostMapping("/recharge")
    public Result<BigDecimal> recharge(
            @RequestBody CoinRechargeRequest request) {
        return Result.success(coinWalletService.recharge(request));
    }

    @GetMapping("/transactions")
    public Result<PageResultVO<CoinTransactionVO>> getTransactions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(
                coinWalletService.getTransactions(pageNum, pageSize));
    }
}