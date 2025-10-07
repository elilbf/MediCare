package com.scheduler.notificationservice.repository;

import com.scheduler.notificationservice.inbound.entity.Notificacoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notificacoes, Long> {
}
