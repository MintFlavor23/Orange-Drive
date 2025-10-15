package com.safedrive.controller;

import com.safedrive.dto.request.NoteRequest;
import com.safedrive.dto.response.NoteResponse;
import com.safedrive.entity.Note;
import com.safedrive.entity.User;
import com.safedrive.service.NoteService;
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
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request) {
        User user = SecurityUtil.getCurrentUser();
        Note note = noteService.createNote(request.getTitle(), request.getContent(), user);

        NoteResponse response = new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedDate(),
                note.getUpdatedDate());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getUserNotes() {
        User user = SecurityUtil.getCurrentUser();
        List<Note> notes = noteService.getUserNotes(user.getId());

        List<NoteResponse> response = notes.stream()
                .map(note -> new NoteResponse(
                        note.getId(),
                        note.getTitle(),
                        note.getContent(),
                        note.getCreatedDate(),
                        note.getUpdatedDate()))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNote(@PathVariable Long noteId) {
        User user = SecurityUtil.getCurrentUser();
        Optional<Note> note = noteService.getNote(noteId, user.getId());

        if (note.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Note noteEntity = note.get();
        NoteResponse response = new NoteResponse(
                noteEntity.getId(),
                noteEntity.getTitle(),
                noteEntity.getContent(),
                noteEntity.getCreatedDate(),
                noteEntity.getUpdatedDate());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long noteId, @Valid @RequestBody NoteRequest request) {
        User user = SecurityUtil.getCurrentUser();
        Note note = noteService.updateNote(noteId, request.getTitle(), request.getContent(), user.getId());

        NoteResponse response = new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedDate(),
                note.getUpdatedDate());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Long noteId) {
        User user = SecurityUtil.getCurrentUser();
        noteService.deleteNote(noteId, user.getId());
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteResponse>> searchNotes(@RequestParam String query) {
        User user = SecurityUtil.getCurrentUser();
        List<Note> notes = noteService.searchNotes(query, user.getId());

        List<NoteResponse> response = notes.stream()
                .map(note -> new NoteResponse(
                        note.getId(),
                        note.getTitle(),
                        note.getContent(),
                        note.getCreatedDate(),
                        note.getUpdatedDate()))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }
}