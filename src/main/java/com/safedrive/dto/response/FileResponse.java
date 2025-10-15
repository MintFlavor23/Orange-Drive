package com.safedrive.dto.response;

import java.time.LocalDateTime;

public class FileResponse {
    private Long id;
    private String filename;
    private String originalName;
    private String contentType;
    private Long size;
    private LocalDateTime uploadDate;

    public FileResponse(Long id, String filename, String originalName, String contentType, Long size,
            LocalDateTime uploadDate) {
        this.id = id;
        this.filename = filename;
        this.originalName = originalName;
        this.contentType = contentType;
        this.size = size;
        this.uploadDate = uploadDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
}
