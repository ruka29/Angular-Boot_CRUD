package com.example.backend.controllers;

import com.example.backend.models.NotificationMessage;
import com.example.backend.models.PasswordResetRequest;
import com.example.backend.models.User;
import com.example.backend.services.NotificationService;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NotificationController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/notify-reset")
    public void notifyPwdReset(PasswordResetRequest passwordResetRequest) {
        String email = passwordResetRequest.getEmail();

        User user = userService.findUserByEmail(email);

        if (user != null) {
            NotificationMessage notificationMessage = new NotificationMessage();

            notificationMessage.setName(user.getName());
            notificationMessage.setEmail(user.getEmail());
            notificationMessage.setUserId(String.valueOf(user.getId()));
            notificationMessage.setType("PASSWORD_RESET_REQUEST");
            notificationMessage.setMessage(user.getName() + " requested a password reset.");
            notificationMessage.setExpanded(false);

            try {
                NotificationMessage savedNotification = notificationService.saveNotification(notificationMessage);

                messagingTemplate.convertAndSend("/topic/notifications", savedNotification);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @GetMapping("/api/notifications")
    public ResponseEntity<Map<String, Object>> getAllNotifications() {
        try {
            List<NotificationMessage> notifications = notificationService.findAll();

            return ResponseEntity.ok(Map.of("notifications", notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid Request!"));
        }
    }

    @PostMapping("/api/notification/update")
    public ResponseEntity<Map<String, Object>> updateNotification(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("ID " + body.get("id"));

        try {
            String message = notificationService.updateNotification(body.get("id"));
            response.put("message", message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
