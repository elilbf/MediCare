package com.scheduler.schedulingservice;

import com.scheduler.schedulingservice.exception.KafkaProducerCheckedException;

public interface IProducerNotificationService {

    <T> void produce(T message) throws KafkaProducerCheckedException;
}
