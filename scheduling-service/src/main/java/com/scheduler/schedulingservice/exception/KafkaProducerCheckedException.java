package com.scheduler.schedulingservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class KafkaProducerCheckedException extends GenericCheckedException{

    @Getter
    private final String topic;
    private final String message;

    public KafkaProducerCheckedException(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }

    public KafkaProducerCheckedException(String topic, String message, Throwable cause) {
        super(message, cause);
        this.topic = topic;
        this.message = message;
    }

}
