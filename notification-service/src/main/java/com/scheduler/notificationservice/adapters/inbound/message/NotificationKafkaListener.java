package com.scheduler.notificationservice.adapters.inbound.message;

import com.scheduler.notificationservice.ports.inbound.IKafkaListener;
import com.scheduler.notificationservice.ports.inbound.INotificationInsertUseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationKafkaListener implements IKafkaListener {

    private final INotificationInsertUseCase notificationInsertUseCase;

    public NotificationKafkaListener(INotificationInsertUseCase notificationInsertUseCase) {
        this.notificationInsertUseCase = notificationInsertUseCase;
    }

    @Override
    @KafkaListener(
            topics = "${kafka.topics.notification}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "${spring.kafka.consumer.concurrency}"
    )
    public void listen(ConsumerRecord<String, String> message) {
        log.info("[NotificationKafkaListener] - Mensagem Recebida: {}", message.value());
        notificationInsertUseCase.syncNotification(message.value());
    }
}
