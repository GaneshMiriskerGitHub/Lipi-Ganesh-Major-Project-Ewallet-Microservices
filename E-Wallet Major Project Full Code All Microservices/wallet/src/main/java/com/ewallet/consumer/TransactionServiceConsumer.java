package com.ewallet.consumer;

import com.ewallet.service.WalletService;
import com.ewallet.service.resource.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Configuration

public class TransactionServiceConsumer {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    WalletService walletService;

    Logger logger = LoggerFactory.getLogger(TransactionServiceConsumer.class);

    @KafkaListener(topics="TRANS_CREATED",groupId = "transactionGroup")
    public void handleTransactions(String message) {
        System.out.println("***************** coming from transaction microservices *********** ");
        System.out.println("***************** because of the topic 'TRANS_CREATED' ");
        logger.info("transaction message received {}", message);
        try{
            Transaction transaction = mapper.readValue(message, Transaction.class);
            System.out.println("************** going to updateWallet in walletService");
            walletService.updateWallet(transaction);
        }catch (JsonProcessingException e) {
            logger.error("error in deserializing the json message");
        }
    }



}
