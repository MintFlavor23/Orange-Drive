package com.safedrive.service;

import com.safedrive.entity.FileEntity;
import com.safedrive.entity.User;
import com.safedrive.repository.FileRepository;
import com.safedrive.exception.FileNotFoundException;
import com.safedrive.exception.FileStorageException;
import com.safedrive.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileRepository fileRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size:50MB}")
    private String maxFileSize;

    public FileEntity uploadFile(MultipartFile file, User user) {
        logger.info("Uploading file '{}' for user ID: {}", file.getOriginalFilename(), user.getId());

        // Validate that the current user matches the provided user
        SecurityUtil.validateUserAccess(user.getId());

        try {
            // Create user-specific upload directory
            Path userUploadPath = Paths.get(uploadDir, "users", user.getId().toString());
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Validate file
            validateFile(file);

            // Generate unique filename
            String filename = generateUniqueFilename(file.getOriginalFilename());
            Path filePath = userUploadPath.resolve(filename);

            // Save file to disk
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create file entity
            FileEntity fileEntity = new FileEntity(
                    filename,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    filePath.toString(),
                    user);

            FileEntity savedFile = fileRepository.save(fileEntity);
            logger.info("File uploaded successfully with ID: {} for user {}", savedFile.getId(), user.getId());

            return savedFile;

        } catch (IOException e) {
            logger.error("Error uploading file: {}", e.getMessage());
            throw new FileStorageException("Failed to upload file: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<FileEntity> getUserFiles(Long userId) {
        // Validate that the current user is requesting their own files
        SecurityUtil.validateUserAccess(userId);
        return fileRepository.findByUserIdOrderByUploadDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public Page<FileEntity> getUserFiles(Long userId, Pageable pageable) {
        // Validate that the current user is requesting their own files
        SecurityUtil.validateUserAccess(userId);
        return fileRepository.findByUserIdOrderByUploadDateDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<FileEntity> getFile(Long fileId, Long userId) {
        // Validate that the current user is requesting their own file
        SecurityUtil.validateUserAccess(userId);
        return fileRepository.findByIdAndUserId(fileId, userId);
    }

    @Transactional(readOnly = true)
    public FileEntity getFileById(Long fileId, Long userId) {
        // Validate that the current user is requesting their own file
        SecurityUtil.validateUserAccess(userId);
        return fileRepository.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));
    }

    public void deleteFile(Long fileId, Long userId) {
        logger.info("Deleting file ID: {} for user ID: {}", fileId, userId);

        // Validate that the current user is deleting their own file
        SecurityUtil.validateUserAccess(userId);

        Optional<FileEntity> fileEntity = fileRepository.findByIdAndUserId(fileId, userId);
        if (fileEntity.isPresent()) {
            try {
                // Delete file from disk
                Path filePath = Paths.get(fileEntity.get().getFilePath());
                Files.deleteIfExists(filePath);

                // Delete from database
                fileRepository.deleteByIdAndUserId(fileId, userId);

                logger.info("File deleted successfully for user {}", userId);
            } catch (IOException e) {
                logger.error("Error deleting file from disk: {}", e.getMessage());
                throw new FileStorageException("Failed to delete file: " + e.getMessage());
            }
        } else {
            throw new FileNotFoundException("File not found with ID: " + fileId);
        }
    }

    @Transactional(readOnly = true)
    public List<FileEntity> searchFiles(String query, Long userId) {
        // Validate that the current user is searching their own files
        SecurityUtil.validateUserAccess(userId);
        return fileRepository.searchFilesByUserId(userId, query);
    }

    @Transactional(readOnly = true)
    public List<FileEntity> getFilesByType(String contentType, Long userId) {
        // Validate that the current user is requesting their own files
        SecurityUtil.validateUserAccess(userId);
        return fileRepository.findByUserIdAndContentType(userId, contentType);
    }

    @Transactional(readOnly = true)
    public Long getTotalStorageUsed(Long userId) {
        // Validate that the current user is requesting their own storage info
        SecurityUtil.validateUserAccess(userId);
        Long totalSize = fileRepository.getTotalSizeByUserId(userId);
        return totalSize != null ? totalSize : 0L;
    }

    @Transactional(readOnly = true)
    public long getFileCount(Long userId) {
        // Validate that the current user is requesting their own file count
        SecurityUtil.validateUserAccess(userId);
        return fileRepository.countByUserId(userId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot upload empty file");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
            throw new FileStorageException("File must have a name");
        }

        // Add more validation as needed (file size, type, etc.)
    }

    private String generateUniqueFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = "";

        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        return uuid + extension;
    }
}
