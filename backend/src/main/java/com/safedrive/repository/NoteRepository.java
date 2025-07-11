package com.safedrive.repository;

import com.safedrive.entity.Note;
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
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserOrderByUpdatedDateDesc(User user);

    List<Note> findByUserIdOrderByUpdatedDateDesc(Long userId);

    Optional<Note> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR n.content LIKE CONCAT('%', :query, '%'))")
    List<Note> searchNotesByUserId(@Param("userId") Long userId, @Param("query") String query);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Note> findByUserIdAndTitleContainingIgnoreCase(@Param("userId") Long userId, @Param("title") String title);

    Page<Note> findByUserIdOrderByUpdatedDateDesc(Long userId, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Note n WHERE n.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}