package com.safedrive.service;

import com.safedrive.entity.Credential;
import com.safedrive.entity.User;
import com.safedrive.repository.CredentialRepository;
import com.safedrive.exception.CredentialNotFoundException;
import com.safedrive.exception.DuplicateCredentialException;
import com.safedrive.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CredentialService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialService.class);

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    public Credential createCredential(String service, String username, String password, String url, String notes,
            User user) {
        logger.info("Creating credential for service '{}' for user ID: {}", service, user.getId());

        // Check if credential already exists for this service
        if (credentialRepository.existsByUserIdAndService(user.getId(), service)) {
            throw new DuplicateCredentialException("Credential already exists for service: " + service);
        }

        String encryptedPassword = encryptionUtil.encrypt(password);
        Credential credential = new Credential(service, username, encryptedPassword, user);
        credential.setUrl(url);
        credential.setNotes(notes);

        Credential savedCredential = credentialRepository.save(credential);
        logger.info("Credential created successfully with ID: {}", savedCredential.getId());

        return savedCredential;
    }

    @Transactional(readOnly = true)
    public List<Credential> getUserCredentials(Long userId) {
        return credentialRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public Page<Credential> getUserCredentials(Long userId, Pageable pageable) {
        return credentialRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Credential> getCredential(Long credentialId, Long userId) {
        return credentialRepository.findByIdAndUserId(credentialId, userId);
    }

    @Transactional(readOnly = true)
    public Credential getCredentialById(Long credentialId, Long userId) {
        return credentialRepository.findByIdAndUserId(credentialId, userId)
                .orElseThrow(() -> new CredentialNotFoundException("Credential not found with ID: " + credentialId));
    }

    @Transactional(readOnly = true)
    public String decryptPassword(String encryptedPassword) {
        return encryptionUtil.decrypt(encryptedPassword);
    }

    public Credential updateCredential(Long credentialId, String service, String username, String password, String url,
            String notes, Long userId) {
        logger.info("Updating credential ID: {} for user ID: {}", credentialId, userId);

        Credential existingCredential = getCredentialById(credentialId, userId);
        existingCredential.setService(service);
        existingCredential.setUsername(username);

        if (password != null && !password.isEmpty()) {
            existingCredential.setEncryptedPassword(encryptionUtil.encrypt(password));
        }

        existingCredential.setUrl(url);
        existingCredential.setNotes(notes);

        Credential updatedCredential = credentialRepository.save(existingCredential);
        logger.info("Credential updated successfully");

        return updatedCredential;
    }

    public void deleteCredential(Long credentialId, Long userId) {
        logger.info("Deleting credential ID: {} for user ID: {}", credentialId, userId);

        if (!credentialRepository.findByIdAndUserId(credentialId, userId).isPresent()) {
            throw new CredentialNotFoundException("Credential not found with ID: " + credentialId);
        }

        credentialRepository.deleteByIdAndUserId(credentialId, userId);
        logger.info("Credential deleted successfully");
    }

    @Transactional(readOnly = true)
    public List<Credential> searchCredentials(String query, Long userId) {
        return credentialRepository.searchCredentialsByUserId(userId, query);
    }

    @Transactional(readOnly = true)
    public long getCredentialCount(Long userId) {
        return credentialRepository.countByUserId(userId);
    }
}
