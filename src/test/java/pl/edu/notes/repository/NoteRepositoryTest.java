package pl.edu.notes.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pl.edu.notes.model.Category;
import pl.edu.notes.model.Note;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest - laduje TYLKO warstwe JPA (repozytoria, encje, baze H2)
// nie laduje kontrolerow ani serwisow - test jest szybszy
// @ActiveProfiles("test") - uzywa osobnego srodowiska testowego (application-test.properties)
@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    // @BeforeEach - wykonuje sie przed KAZDYM testem
    // czysci baze i tworzy dane testowe od nowa
    // kolejnosc: najpierw notatki (bo maja klucz obcy), potem kategorie
    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
        categoryRepository.deleteAll();

        category = new Category("Uczelnia");
        category = categoryRepository.save(category);

        Note note1 = new Note("Projekt z baz danych", "Oddac do piatku");
        note1.setCategory(category);
        noteRepository.save(note1);

        Note note2 = new Note("Zakupy na weekend", "Mleko, chleb");
        noteRepository.save(note2);

        Note note3 = new Note("Notatka z baz danych", "SQL, JPA, Hibernate");
        note3.setCategory(category);
        noteRepository.save(note3);
    }

    // Test: zapisanie notatki do bazy i sprawdzenie czy dostala id
    @Test
    void save_shouldPersistNote() {
        Note note = new Note("Nowa notatka", "Tresc");
        Note saved = noteRepository.save(note);

        assertNotNull(saved.getId());
        assertEquals("Nowa notatka", saved.getTitle());
        assertEquals("Tresc", saved.getContent());
    }

    // Test: pobranie wszystkich notatek z bazy
    @Test
    void findAll_shouldReturnAllNotes() {
        List<Note> notes = noteRepository.findAll();

        assertEquals(3, notes.size());
    }

    // Test: pobranie notatki po id
    @Test
    void findById_shouldReturnNote_whenExists() {
        Note saved = noteRepository.save(new Note("Testowa", "Tresc"));

        Optional<Note> found = noteRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Testowa", found.get().getTitle());
    }

    // Test: pobranie nieistniejacej notatki zwraca pusty Optional
    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Note> found = noteRepository.findById(9999L);

        assertFalse(found.isPresent());
    }

    // Test: wyszukiwanie notatek po fragmencie tytulu (ignore case)
    @Test
    void findByTitleContainingIgnoreCase_shouldFindMatching() {
        List<Note> results = noteRepository.findByTitleContainingIgnoreCase("baz danych");

        assertEquals(2, results.size());
    }

    // Test: wyszukiwanie ignoruje wielkosc liter
    @Test
    void findByTitleContainingIgnoreCase_shouldIgnoreCase() {
        List<Note> results = noteRepository.findByTitleContainingIgnoreCase("ZAKUPY");

        assertEquals(1, results.size());
        assertEquals("Zakupy na weekend", results.get(0).getTitle());
    }

    // Test: wyszukiwanie zwraca pusta liste gdy brak wynikow
    @Test
    void findByTitleContainingIgnoreCase_shouldReturnEmpty_whenNoMatch() {
        List<Note> results = noteRepository.findByTitleContainingIgnoreCase("xyz123");

        assertTrue(results.isEmpty());
    }

    // Test: aktualizacja notatki w bazie
    @Test
    void save_shouldUpdateExistingNote() {
        Note saved = noteRepository.save(new Note("Stary tytul", "Stara tresc"));
        saved.setTitle("Nowy tytul");
        Note updated = noteRepository.save(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Nowy tytul", updated.getTitle());
    }

    // Test: usuwanie notatki z bazy
    @Test
    void delete_shouldRemoveNote() {
        Note saved = noteRepository.save(new Note("Do usuniecia", "test"));
        Long id = saved.getId();

        noteRepository.delete(saved);

        assertFalse(noteRepository.findById(id).isPresent());
    }

    // Test: notatka z przypisana kategoria
    @Test
    void save_shouldPersistNoteWithCategory() {
        Note note = new Note("Z kategoria", "Tresc");
        note.setCategory(category);
        Note saved = noteRepository.save(note);

        Note found = noteRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(found.getCategory());
        assertEquals("Uczelnia", found.getCategory().getName());
    }

    // Test: notatka bez kategorii (category = null)
    @Test
    void save_shouldPersistNoteWithoutCategory() {
        Note note = new Note("Bez kategorii", "Tresc");
        Note saved = noteRepository.save(note);

        Note found = noteRepository.findById(saved.getId()).orElseThrow();
        assertNull(found.getCategory());
    }

    // Test: count zwraca poprawna liczbe rekordow
    @Test
    void count_shouldReturnNumberOfNotes() {
        long count = noteRepository.count();

        assertEquals(3, count);
    }
}
