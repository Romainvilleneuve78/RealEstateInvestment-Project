package com.propertystake.controller;

import com.propertystake.model.Wallet;
import com.propertystake.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PutMapping("/{walletId}/balance")
    public Wallet updateBalance(@PathVariable Long walletId, @RequestBody BigDecimal amount) {
        return walletService.updateBalance(walletId, amount);
    }
}
