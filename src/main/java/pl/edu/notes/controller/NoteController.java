package pl.edu.notes.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.notes.model.Category;
import pl.edu.notes.model.Note;
import pl.edu.notes.repository.CategoryRepository;
import pl.edu.notes.repository.NoteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;

    public NoteController(NoteRepository noteRepository, CategoryRepository categoryRepository) {
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) {
        Note savedNote = noteRepository.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @Valid @RequestBody Note noteDetails) {
        return noteRepository.findById(id)
                .map(note -> {
                    note.setTitle(noteDetails.getTitle());
                    note.setContent(noteDetails.getContent());
                    note.setCategory(noteDetails.getCategory());
                    Note updatedNote = noteRepository.save(note);
                    return ResponseEntity.ok(updatedNote);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> {
                    noteRepository.delete(note);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/notes/{noteId}/category/{categoryId} - przypisuje kategorie do notatki
    @PutMapping("/{noteId}/category/{categoryId}")
    public ResponseEntity<Note> assignCategory(@PathVariable Long noteId, @PathVariable Long categoryId) {
        return noteRepository.findById(noteId)
                .map(note -> {
                    Category category = categoryRepository.findById(categoryId)
                            .orElse(null);
                    if (category == null) {
                        return ResponseEntity.notFound().<Note>build();
                    }
                    note.setCategory(category);
                    Note updated = noteRepository.save(note);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Note> searchNotes(@RequestParam String title) {
        return noteRepository.findByTitleContainingIgnoreCase(title);
    }
}
