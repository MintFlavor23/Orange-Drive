package com.safedrive.repository;

import com.safedrive.entity.Credential;
import com.safedrive.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {

    List<Credential> findByUserOrderByCreatedDateDesc(User user);

    List<Credential> findByUserIdOrderByCreatedDateDesc(Long userId);

    Optional<Credential> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

    @Query("SELECT c FROM Credential c WHERE c.user.id = :userId AND " +
            "(LOWER(c.service) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.username) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Credential> searchCredentialsByUserId(@Param("userId") Long userId, @Param("query") String query);

    @Query("SELECT c FROM Credential c WHERE c.user.id = :userId AND LOWER(c.service) LIKE LOWER(CONCAT('%', :service, '%'))")
    List<Credential> findByUserIdAndServiceContainingIgnoreCase(@Param("userId") Long userId,
            @Param("service") String service);

    Page<Credential> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Credential c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndService(Long userId, String service);
}