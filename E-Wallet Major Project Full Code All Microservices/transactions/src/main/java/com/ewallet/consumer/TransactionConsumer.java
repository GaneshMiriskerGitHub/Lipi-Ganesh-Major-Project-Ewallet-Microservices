package com.ewallet.consumer;

import com.ewallet.service.TransactionService;
import com.ewallet.service.resource.TransactionMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {


    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    TransactionService transactionService;


    @KafkaListener(topics = "WALLET_UPDATE", groupId = "transactionGrp")
    public void updateTransaction(String message) {
        System.out.println("********************** WALLET_UPDATE topic enabled *******************");
        try {
            TransactionMessage message1 = mapper.readValue(message, TransactionMessage.class);
            System.out.println("************** about to update transaction ***************");
            transactionService.updateTransaction(message1);
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
