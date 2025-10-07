package com.scheduler.notificationservice.ports.inbound;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IKafkaListener {

    void listen(ConsumerRecord<String, String> message);
}
