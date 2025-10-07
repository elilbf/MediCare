package com.scheduler.notificationservice.ports.outbound;

import com.scheduler.notificationservice.dto.NotificationDTO;

public interface INotificationPersistencePort {

    void insertMessage(NotificationDTO notification);

}
