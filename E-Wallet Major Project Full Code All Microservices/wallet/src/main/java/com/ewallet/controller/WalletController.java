package com.ewallet.controller;

import com.ewallet.entity.Wallet;
import com.ewallet.service.WalletService;
import com.ewallet.service.resource.WalletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "wallet")
public class WalletController {


    @Autowired
    WalletService walletService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserWallet(@PathVariable("userId") String userId) {
        System.out.println("wallerController userId = " + userId);
        Wallet wallet = walletService.getUserWallet(userId);

        WalletResponse walletResponse = wallet.toWalletResponse();

        return new ResponseEntity<>(walletResponse, HttpStatusCode.valueOf(200));
    }

}
