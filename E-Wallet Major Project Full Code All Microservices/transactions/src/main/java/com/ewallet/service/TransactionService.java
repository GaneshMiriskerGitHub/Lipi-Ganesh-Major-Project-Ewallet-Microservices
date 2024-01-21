package com.ewallet.service;


import com.ewallet.entity.Transaction;
import com.ewallet.service.resource.TransactionMessage;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {


    void createTransaction(Transaction transaction);


    void updateTransaction(TransactionMessage transactionMessage);



}
