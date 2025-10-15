package com.safedrive.repository;

import com.safedrive.entity.FileEntity;
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
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findByUserOrderByUploadDateDesc(User user);

    List<FileEntity> findByUserIdOrderByUploadDateDesc(Long userId);

    Optional<FileEntity> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

    @Query("SELECT f FROM FileEntity f WHERE f.user.id = :userId AND " +
            "(LOWER(f.originalName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.filename) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<FileEntity> searchFilesByUserId(@Param("userId") Long userId, @Param("query") String query);

    @Query("SELECT f FROM FileEntity f WHERE f.user.id = :userId AND f.contentType = :contentType")
    List<FileEntity> findByUserIdAndContentType(@Param("userId") Long userId, @Param("contentType") String contentType);

    @Query("SELECT SUM(f.size) FROM FileEntity f WHERE f.user.id = :userId")
    Long getTotalSizeByUserId(@Param("userId") Long userId);

    Page<FileEntity> findByUserIdOrderByUploadDateDesc(Long userId, Pageable pageable);

    @Query("SELECT COUNT(f) FROM FileEntity f WHERE f.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}