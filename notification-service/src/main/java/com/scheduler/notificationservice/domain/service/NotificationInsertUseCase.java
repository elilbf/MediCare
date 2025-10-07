package com.scheduler.notificationservice.domain.service;

import com.scheduler.notificationservice.ports.inbound.INotificationInsertUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationInsertUseCase implements INotificationInsertUseCase {

    @Override
    public void syncNotification(String message) {

    }
}
