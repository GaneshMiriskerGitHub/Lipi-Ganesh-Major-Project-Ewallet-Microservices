package com.ewallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguration {

    @Bean
    public JavaMailSenderImpl getJavaMailSenderImpl() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("miriskerg@gmail.com");
        mailSender.setPassword("jhxvuzfztgdvxfkc");
        mailSender.setHost("smtp.gmail.com"); // just like http , we have for mail services host will be SMTP protocol
        mailSender.setPort(587);
        mailSender.setProtocol("smtp");

        mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "true");
        mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", "true");


        return mailSender;


    }

}
