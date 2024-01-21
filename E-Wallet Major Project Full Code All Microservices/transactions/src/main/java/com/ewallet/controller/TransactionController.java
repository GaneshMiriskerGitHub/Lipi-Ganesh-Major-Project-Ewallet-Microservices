package com.ewallet.controller;

import com.ewallet.dto.TransactionRequest;
import com.ewallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {


    @Autowired
    TransactionService transactionService;


    @PostMapping("/transfer")
    public ResponseEntity<?> performTransfer(@RequestBody TransactionRequest transactionRequest) {

        System.out.println("************ TransactionController ************");
        System.out.println("******************* performTransaction : TransactionRequest ");

        transactionService.createTransaction(transactionRequest.toTransaction());

        return new ResponseEntity<>(null, HttpStatus.CREATED);

    }


}
