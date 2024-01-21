package com.ewallet.service;

import com.ewallet.service.resource.NotificationMessage;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    public void sendNotification(NotificationMessage notificationMessage);

}
