package com.ewallet.consumer;

import com.ewallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserServiceConsumer {

    @Autowired
    WalletService walletService;

    Logger logger = LoggerFactory.getLogger(UserServiceConsumer.class);


    @KafkaListener(topics = "USER_CREATED", groupId = "walletGrp")
    public void createNewUserWallet(String message) {
        logger.info("C1 creationWallet recevied message: {}" , message);
        walletService.createNewWallet(message);
    }


    @KafkaListener(topics = "USER_DELETED", groupId = "walletGrp")
    public void disableWalletForUser(String message) {
        logger.info("C2 deleteWallet recevied message: {}" , message);
        walletService.disableActiveWallet(message);
    }

    @KafkaListener(topics = "TRANSACTION", groupId = "walletGrp")
    public void updateTransaction(String message) {
        logger.info("C3 transaction received message: {}" , message);
        walletService.disableActiveWallet(message);
    }

}
