package com.safedrive.controller;

import com.safedrive.dto.response.FileResponse;
import com.safedrive.entity.FileEntity;
import com.safedrive.entity.User;
import com.safedrive.service.FileService;
import com.safedrive.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("File upload request: {}", file.getOriginalFilename());

        User user = SecurityUtil.getCurrentUser();
        FileEntity uploadedFile = fileService.uploadFile(file, user);

        FileResponse response = new FileResponse(
                uploadedFile.getId(),
                uploadedFile.getFilename(),
                uploadedFile.getOriginalName(),
                uploadedFile.getContentType(),
                uploadedFile.getSize(),
                uploadedFile.getUploadDate());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<FileResponse>> getUserFiles() {
        User user = SecurityUtil.getCurrentUser();
        List<FileEntity> files = fileService.getUserFiles(user.getId());

        List<FileResponse> response = files.stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getFilename(),
                        file.getOriginalName(),
                        file.getContentType(),
                        file.getSize(),
                        file.getUploadDate()))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            User user = SecurityUtil.getCurrentUser();
            Optional<FileEntity> fileEntity = fileService.getFile(fileId, user.getId());

            if (fileEntity.isEmpty()) {
                logger.warn("File not found for user: fileId={}, userId={}", fileId, user.getId());
                return ResponseEntity.notFound().build();
            }

            FileEntity file = fileEntity.get();
            Path filePath = Paths.get(file.getFilePath()).toAbsolutePath();
            logger.info("Attempting to download file: path={}, exists={}", filePath, filePath.toFile().exists());

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                logger.error("File not accessible: path={}, exists={}, readable={}",
                        filePath, resource.exists(), resource.isReadable());
                return ResponseEntity.notFound().build();
            }

            logger.info("File download successful: {}", file.getOriginalName());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(resource);

        } catch (MalformedURLException e) {
            logger.error("Error downloading file: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        User user = SecurityUtil.getCurrentUser();
        fileService.deleteFile(fileId, user.getId());
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileResponse>> searchFiles(@RequestParam String query) {
        User user = SecurityUtil.getCurrentUser();
        List<FileEntity> files = fileService.searchFiles(query, user.getId());

        List<FileResponse> response = files.stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getFilename(),
                        file.getOriginalName(),
                        file.getContentType(),
                        file.getSize(),
                        file.getUploadDate()))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }
}