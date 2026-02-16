package pl.edu.notes.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Testy jednostkowe klasy Category - nie wymagaja Springa ani bazy danych
class CategoryTest {

    // Test: konstruktor bezargumentowy tworzy pusty obiekt
    @Test
    void defaultConstructor_shouldCreateEmptyCategory() {
        Category category = new Category();

        assertNull(category.getId());
        assertNull(category.getName());
        assertNull(category.getNotes());
    }

    // Test: konstruktor z parametrem ustawia nazwe
    @Test
    void parameterizedConstructor_shouldSetName() {
        Category category = new Category("Uczelnia");

        assertEquals("Uczelnia", category.getName());
    }

    // Test: setter i getter dla id
    @Test
    void setId_shouldUpdateId() {
        Category category = new Category();
        category.setId(1L);

        assertEquals(1L, category.getId());
    }

    // Test: setter i getter dla name
    @Test
    void setName_shouldUpdateName() {
        Category category = new Category();
        category.setName("Praca");

        assertEquals("Praca", category.getName());
    }

    // Test: setter i getter dla listy notatek
    @Test
    void setNotes_shouldUpdateNotesList() {
        Category category = new Category("Osobiste");
        List<Note> notes = new ArrayList<>();
        notes.add(new Note("Notatka 1", "Tresc 1"));
        notes.add(new Note("Notatka 2", "Tresc 2"));

        category.setNotes(notes);

        assertEquals(2, category.getNotes().size());
    }

    // Test: kategoria bez notatek - pusta lista
    @Test
    void setNotes_shouldAcceptEmptyList() {
        Category category = new Category("Pusta");
        category.setNotes(new ArrayList<>());

        assertNotNull(category.getNotes());
        assertTrue(category.getNotes().isEmpty());
    }

    // Test: zmiana nazwy kategorii
    @Test
    void setName_shouldAllowNameChange() {
        Category category = new Category("Stara nazwa");
        category.setName("Nowa nazwa");

        assertEquals("Nowa nazwa", category.getName());
    }
}
