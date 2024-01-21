package com.ewallet.serviceImpl;

import com.ewallet.service.NotificationService;
import com.ewallet.service.resource.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {


    @Autowired
    JavaMailSenderImpl javaMailSender;


    @Override
    public void sendNotification(NotificationMessage notificationMessage) {

        try {
            sendEmail(notificationMessage);
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sendSms(notificationMessage);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendSms(NotificationMessage notificationMessage) {
        log.info("******************");
    }

    private void sendEmail(NotificationMessage notificationMessage) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(notificationMessage.getToEmail());
        mailMessage.setSubject(notificationMessage.getSubject());
        mailMessage.setText(notificationMessage.getBody());
        javaMailSender.send(mailMessage);

        System.out.println("********************************* email has been sent ***************************");

    }
}
