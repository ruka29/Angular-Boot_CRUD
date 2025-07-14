package com.example.backend.services;

import com.example.backend.models.NotificationMessage;
import com.example.backend.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationMessage saveNotification(NotificationMessage notificationMessage) {
        return notificationRepository.save(notificationMessage);
    }

    public List<NotificationMessage> findAll() {
        return notificationRepository.findAll();
    }

    public String updateNotification(String id) {
        NotificationMessage existingNotification = notificationRepository.findById(Long.valueOf(id)).orElse(null);

        if (existingNotification != null) {
            try {
                existingNotification.setRead(true);

                notificationRepository.save(existingNotification);
                return "Notification updated successfully!";
            } catch (Exception e) {
                return "Failed to update notification!";
            }
        } else {
            return "Notification does not exist!";
        }
    }
}
