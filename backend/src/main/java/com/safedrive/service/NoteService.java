package com.safedrive.service;

import com.safedrive.entity.Note;
import com.safedrive.entity.User;
import com.safedrive.repository.NoteRepository;
import com.safedrive.exception.NoteNotFoundException;
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
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    @Autowired
    private NoteRepository noteRepository;

    public Note createNote(String title, String content, User user) {
        logger.info("Creating note '{}' for user ID: {}", title, user.getId());

        Note note = new Note(title, content, user);
        Note savedNote = noteRepository.save(note);

        logger.info("Note created successfully with ID: {}", savedNote.getId());
        return savedNote;
    }

    @Transactional(readOnly = true)
    public List<Note> getUserNotes(Long userId) {
        return noteRepository.findByUserIdOrderByUpdatedDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public Page<Note> getUserNotes(Long userId, Pageable pageable) {
        return noteRepository.findByUserIdOrderByUpdatedDateDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Note> getNote(Long noteId, Long userId) {
        return noteRepository.findByIdAndUserId(noteId, userId);
    }

    @Transactional(readOnly = true)
    public Note getNoteById(Long noteId, Long userId) {
        return noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with ID: " + noteId));
    }

    public Note updateNote(Long noteId, String title, String content, Long userId) {
        logger.info("Updating note ID: {} for user ID: {}", noteId, userId);

        Note existingNote = getNoteById(noteId, userId);
        existingNote.setTitle(title);
        existingNote.setContent(content);

        Note updatedNote = noteRepository.save(existingNote);
        logger.info("Note updated successfully");

        return updatedNote;
    }

    public void deleteNote(Long noteId, Long userId) {
        logger.info("Deleting note ID: {} for user ID: {}", noteId, userId);

        if (!noteRepository.findByIdAndUserId(noteId, userId).isPresent()) {
            throw new NoteNotFoundException("Note not found with ID: " + noteId);
        }

        noteRepository.deleteByIdAndUserId(noteId, userId);
        logger.info("Note deleted successfully");
    }

    @Transactional(readOnly = true)
    public List<Note> searchNotes(String query, Long userId) {
        return noteRepository.searchNotesByUserId(userId, query);
    }

    @Transactional(readOnly = true)
    public long getNoteCount(Long userId) {
        return noteRepository.countByUserId(userId);
    }
}