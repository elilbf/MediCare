package com.scheduler.notificationservice.adapters.outbound;

import com.scheduler.notificationservice.adapters.inbound.mapper.NotificationMapper;
import com.scheduler.notificationservice.dto.NotificationDTO;
import com.scheduler.notificationservice.ports.outbound.INotificationPersistencePort;
import com.scheduler.notificationservice.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Transactional
public class NotificationPersistenceAdapter implements INotificationPersistencePort {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    public NotificationPersistenceAdapter(NotificationMapper notificationMapper, NotificationRepository notificationRepository) {
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void insertMessage(NotificationDTO notification) {
        var entity = notificationMapper.toEntity(notification);

        log.info("[NotificationPersistenceAdapter] - Salvando notificação para o paciente: {}", entity.getNomePaciente());
        notificationRepository.save(entity);
    }
}
