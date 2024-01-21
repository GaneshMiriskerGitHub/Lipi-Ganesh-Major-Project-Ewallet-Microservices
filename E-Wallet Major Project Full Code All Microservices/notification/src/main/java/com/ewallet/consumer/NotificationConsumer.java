package com.ewallet.consumer;

import com.ewallet.service.NotificationService;
import com.ewallet.service.resource.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    NotificationService notificationService;

    @KafkaListener(topics = "NOTIFICATION", groupId = "notificationGrp")
    public void sendNotification(String message) {

        System.out.println("*********************** topic : NOTIFICATION *******************");

        try {
            NotificationMessage notificationMessage = mapper.readValue(message, NotificationMessage.class);

            System.out.println("*************** about to run notification code ************* ");

            notificationService.sendNotification(notificationMessage);
        }catch (Exception e) {

        }

    }

}
