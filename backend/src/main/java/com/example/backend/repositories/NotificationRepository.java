package com.example.backend.repositories;

import com.example.backend.models.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<NotificationMessage, Long> {
}
