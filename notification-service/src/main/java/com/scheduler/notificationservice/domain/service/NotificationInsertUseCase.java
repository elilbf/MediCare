package com.scheduler.notificationservice.domain.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.notificationservice.dto.NotificationDTO;
import com.scheduler.notificationservice.ports.inbound.INotificationInsertUseCase;
import com.scheduler.notificationservice.ports.outbound.INotificationPersistencePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationInsertUseCase implements INotificationInsertUseCase {

    private final INotificationPersistencePort notificationPersistencePort;

    private final ObjectMapper mapper;

    public NotificationInsertUseCase(INotificationPersistencePort notificationPersistencePort, ObjectMapper mapper) {
        this.notificationPersistencePort = notificationPersistencePort;
        this.mapper = mapper;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void syncNotification(String message) {
        NotificationDTO notificationDTO = mapper.convertValue(message, NotificationDTO.class);
        notificationPersistencePort.insertMessage(notificationDTO);

        log.info("[NotificationInsertUseCase] - Notificação enviada para o paciente: {}, no email: {}",
                notificationDTO.getNomePaciente(), notificationDTO.getDataHoraConsulta());
    }

}
