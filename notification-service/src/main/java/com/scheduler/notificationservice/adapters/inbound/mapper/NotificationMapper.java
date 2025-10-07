package com.scheduler.notificationservice.adapters.inbound.mapper;

import com.scheduler.notificationservice.inbound.entity.Notificacoes;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public Notificacoes toEntity(com.scheduler.notificationservice.dto.NotificationDTO dto) {
        if (dto == null) {
            return null;
        }

        return Notificacoes.builder()
                .nomePaciente(dto.getNomePaciente())
                .mensagem(dto.toString())
                .dataCriacao(LocalDateTime.now())
                .build();
    }

}
