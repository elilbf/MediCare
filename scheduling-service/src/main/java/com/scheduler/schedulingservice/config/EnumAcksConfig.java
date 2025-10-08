package com.scheduler.schedulingservice.config;

import lombok.Getter;

@Getter
public enum EnumAcksConfig {

    LEADER("1");

    private final String value;

    EnumAcksConfig(String value) {
        this.value = value;
    }

}
