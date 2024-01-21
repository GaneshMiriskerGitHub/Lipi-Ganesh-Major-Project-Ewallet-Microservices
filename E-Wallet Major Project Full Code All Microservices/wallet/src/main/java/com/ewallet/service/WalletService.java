package com.ewallet.service;


import com.ewallet.entity.Wallet;
import com.ewallet.service.resource.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface WalletService {


    Wallet getUserWallet(String userId);

    Wallet createNewWallet(String userId);

    Wallet disableActiveWallet(String usreId);

    void updateWallet(Transaction transaction) throws JsonProcessingException;


}
