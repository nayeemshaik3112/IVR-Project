package com.example.ivr.notification.repository;

import com.example.ivr.notification.entity.NotificationLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    Optional<NotificationLog> findByEventId(String eventId);
}
