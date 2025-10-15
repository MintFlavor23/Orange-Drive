package com.safedrive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CredentialRequest {
    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name cannot exceed 100 characters")
    private String service;

    @NotBlank(message = "Username is required")
    @Size(max = 255, message = "Username cannot exceed 255 characters")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Size(max = 500, message = "URL cannot exceed 500 characters")
    private String url;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    // Constructors
    public CredentialRequest() {
    }

    public CredentialRequest(String service, String username, String password, String url, String notes) {
        this.service = service;
        this.username = username;
        this.password = password;
        this.url = url;
        this.notes = notes;
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
