package com.scheduler.schedulingservice.service;

import com.scheduler.schedulingservice.exception.KafkaProducerCheckedException;

public interface IProducerNotificationService {

    <T> void produce(T message) throws KafkaProducerCheckedException;
}
