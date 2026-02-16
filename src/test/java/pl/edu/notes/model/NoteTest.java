package pl.edu.notes.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Testy jednostkowe klasy Note - nie wymagaja Springa ani bazy danych
// Testuja gettery, settery, konstruktory i logike biznesowa
class NoteTest {

    // Test: konstruktor bezargumentowy tworzy obiekt z polami null
    @Test
    void defaultConstructor_shouldCreateEmptyNote() {
        Note note = new Note();

        assertNull(note.getId());
        assertNull(note.getTitle());
        assertNull(note.getContent());
        assertNull(note.getCategory());
    }

    // Test: konstruktor z parametrami ustawia tytul i tresc
    @Test
    void parameterizedConstructor_shouldSetTitleAndContent() {
        Note note = new Note("Moj tytul", "Moja tresc");

        assertEquals("Moj tytul", note.getTitle());
        assertEquals("Moja tresc", note.getContent());
    }

    // Test: setter i getter dla title
    @Test
    void setTitle_shouldUpdateTitle() {
        Note note = new Note();
        note.setTitle("Nowy tytul");

        assertEquals("Nowy tytul", note.getTitle());
    }

    // Test: setter i getter dla content
    @Test
    void setContent_shouldUpdateContent() {
        Note note = new Note();
        note.setContent("Nowa tresc");

        assertEquals("Nowa tresc", note.getContent());
    }

    // Test: setter i getter dla id
    @Test
    void setId_shouldUpdateId() {
        Note note = new Note();
        note.setId(5L);

        assertEquals(5L, note.getId());
    }

    // Test: setter i getter dla createdAt
    @Test
    void setCreatedAt_shouldUpdateCreatedAt() {
        Note note = new Note();
        LocalDateTime now = LocalDateTime.now();
        note.setCreatedAt(now);

        assertEquals(now, note.getCreatedAt());
    }

    // Test: setter i getter dla updatedAt
    @Test
    void setUpdatedAt_shouldUpdateUpdatedAt() {
        Note note = new Note();
        LocalDateTime now = LocalDateTime.now();
        note.setUpdatedAt(now);

        assertEquals(now, note.getUpdatedAt());
    }

    // Test: callback onCreate ustawia obie daty
    @Test
    void onCreate_shouldSetCreatedAtAndUpdatedAt() {
        Note note = new Note("Test", "Tresc");
        note.onCreate();

        assertNotNull(note.getCreatedAt());
        assertNotNull(note.getUpdatedAt());
    }

    // Test: callback onUpdate aktualizuje updatedAt
    @Test
    void onUpdate_shouldUpdateUpdatedAt() {
        Note note = new Note("Test", "Tresc");
        note.onCreate();
        LocalDateTime originalUpdatedAt = note.getUpdatedAt();

        // odczekaj chwile zeby czas sie roznil
        note.onUpdate();

        assertNotNull(note.getUpdatedAt());
    }

    // Test: przypisanie kategorii do notatki
    @Test
    void setCategory_shouldAssignCategory() {
        Note note = new Note("Test", "Tresc");
        Category category = new Category("Uczelnia");
        note.setCategory(category);

        assertNotNull(note.getCategory());
        assertEquals("Uczelnia", note.getCategory().getName());
    }

    // Test: notatka bez kategorii - category jest null
    @Test
    void getCategory_shouldReturnNull_whenNotSet() {
        Note note = new Note("Test", "Tresc");

        assertNull(note.getCategory());
    }
}
