package com.scheduler.schedulingservice;

import com.scheduler.schedulingservice.exception.KafkaProducerCheckedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProducerNotificationNotificationService implements IProducerNotificationService {
    
    private final String notificationTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProducerNotificationNotificationService(@Value("${kafka.topics.notification}") String notificationTopic, KafkaTemplate<String, Object> kafkaTemplate) {
        this.notificationTopic = notificationTopic;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplate.setMessageConverter(new JsonMessageConverter());
    }

    @Override
    public <T> void produce(T message) throws KafkaProducerCheckedException {
        try {
            log.info("Enviando mensagem para o tópico Kafka: {}", this.notificationTopic);
            kafkaTemplate.send(this.notificationTopic, message);
        } catch (Exception ex) {
            log.error("Erro ao enviar mensagem para o tópico Kafka: {}", this.notificationTopic, ex);
            throw new KafkaProducerCheckedException(ex.getMessage(), this.notificationTopic);
        }

    }
}
