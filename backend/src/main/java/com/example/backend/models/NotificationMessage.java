package com.example.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String message;
    private String type;
    private String userId;
    private String email;
    private String name;
    private boolean isExpanded;
    private boolean isRead;

    public NotificationMessage() {}

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
    public boolean isRead() {
        return isRead;
    }
}
