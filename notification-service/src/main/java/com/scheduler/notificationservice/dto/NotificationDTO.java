package com.scheduler.notificationservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class NotificationDTO {
    private String nomePaciente;
    private String emailPaciente;
    private String nomeMedico;
    private String dataHoraConsulta;
}
