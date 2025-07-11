package com.safedrive.dto.response;

import java.time.LocalDateTime;

public class CredentialResponse {
    private Long id;
    private String service;
    private String username;
    private String url;
    private String notes;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public CredentialResponse(Long id, String service, String username, String url, String notes,
            LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.service = service;
        this.username = username;
        this.url = url;
        this.notes = notes;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
