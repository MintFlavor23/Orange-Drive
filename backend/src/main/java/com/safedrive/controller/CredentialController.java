package com.safedrive.controller;

import com.safedrive.dto.request.CredentialRequest;
import com.safedrive.dto.response.CredentialResponse;
import com.safedrive.entity.Credential;
import com.safedrive.entity.User;
import com.safedrive.service.CredentialService;
import com.safedrive.util.SecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/credentials")
@CrossOrigin(origins = "*")
public class CredentialController {

        private static final Logger logger = LoggerFactory.getLogger(CredentialController.class);

        @Autowired
        private CredentialService credentialService;

        @PostMapping
        public ResponseEntity<CredentialResponse> createCredential(@Valid @RequestBody CredentialRequest request) {
                User user = SecurityUtil.getCurrentUser();
                Credential credential = credentialService.createCredential(
                                request.getService(),
                                request.getUsername(),
                                request.getPassword(),
                                request.getUrl(),
                                request.getNotes(),
                                user);

                CredentialResponse response = new CredentialResponse(
                                credential.getId(),
                                credential.getService(),
                                credential.getUsername(),
                                credential.getUrl(),
                                credential.getNotes(),
                                credential.getCreatedDate(),
                                credential.getUpdatedDate());

                return ResponseEntity.ok()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .body(response);
        }

        @GetMapping
        public ResponseEntity<List<CredentialResponse>> getUserCredentials() {
                User user = SecurityUtil.getCurrentUser();
                List<Credential> credentials = credentialService.getUserCredentials(user.getId());

                List<CredentialResponse> response = credentials.stream()
                                .map(credential -> new CredentialResponse(
                                                credential.getId(),
                                                credential.getService(),
                                                credential.getUsername(),
                                                credential.getUrl(),
                                                credential.getNotes(),
                                                credential.getCreatedDate(),
                                                credential.getUpdatedDate()))
                                .collect(Collectors.toList());

                return ResponseEntity.ok()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .body(response);
        }

        @GetMapping("/{credentialId}")
        public ResponseEntity<CredentialResponse> getCredential(@PathVariable Long credentialId) {
                User user = SecurityUtil.getCurrentUser();
                Optional<Credential> credential = credentialService.getCredential(credentialId, user.getId());

                if (credential.isEmpty()) {
                        return ResponseEntity.notFound().build();
                }

                Credential credentialEntity = credential.get();
                CredentialResponse response = new CredentialResponse(
                                credentialEntity.getId(),
                                credentialEntity.getService(),
                                credentialEntity.getUsername(),
                                credentialEntity.getUrl(),
                                credentialEntity.getNotes(),
                                credentialEntity.getCreatedDate(),
                                credentialEntity.getUpdatedDate());

                return ResponseEntity.ok()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .body(response);
        }

        @GetMapping("/{credentialId}/password")
        public ResponseEntity<String> getDecryptedPassword(@PathVariable Long credentialId) {
                User user = SecurityUtil.getCurrentUser();
                Optional<Credential> credential = credentialService.getCredential(credentialId, user.getId());

                if (credential.isEmpty()) {
                        return ResponseEntity.notFound().build();
                }

                String decryptedPassword = credentialService.decryptPassword(credential.get().getEncryptedPassword());
                return ResponseEntity.ok()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .body(decryptedPassword);
        }

        @PutMapping("/{credentialId}")
        public ResponseEntity<CredentialResponse> updateCredential(@PathVariable Long credentialId,
                        @Valid @RequestBody CredentialRequest request) {
                User user = SecurityUtil.getCurrentUser();
                Credential credential = credentialService.updateCredential(
                                credentialId,
                                request.getService(),
                                request.getUsername(),
                                request.getPassword(),
                                request.getUrl(),
                                request.getNotes(),
                                user.getId());

                CredentialResponse response = new CredentialResponse(
                                credential.getId(),
                                credential.getService(),
                                credential.getUsername(),
                                credential.getUrl(),
                                credential.getNotes(),
                                credential.getCreatedDate(),
                                credential.getUpdatedDate());

                return ResponseEntity.ok()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .body(response);
        }

        @DeleteMapping("/{credentialId}")
        public ResponseEntity<?> deleteCredential(@PathVariable Long credentialId) {
                User user = SecurityUtil.getCurrentUser();
                credentialService.deleteCredential(credentialId, user.getId());
                return ResponseEntity.ok()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .build();
        }

        @GetMapping("/search")
        public ResponseEntity<List<CredentialResponse>> searchCredentials(@RequestParam String query) {
                User user = SecurityUtil.getCurrentUser();
                List<Credential> credentials = credentialService.searchCredentials(query, user.getId());

                List<CredentialResponse> response = credentials.stream()
                                .map(credential -> new CredentialResponse(
                                                credential.getId(),
                                                credential.getService(),
                                                credential.getUsername(),
                                                credential.getUrl(),
                                                credential.getNotes(),
                                                credential.getCreatedDate(),
                                                credential.getUpdatedDate()))
                                .collect(Collectors.toList());

                return ResponseEntity.ok()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .body(response);
        }
}