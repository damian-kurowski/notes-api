package pl.edu.notes.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.edu.notes.model.Category;
import pl.edu.notes.model.Note;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest - laduje TYLKO warstwe JPA (repozytoria, encje, baze H2)
// @ActiveProfiles("test") - uzywa osobnego srodowiska testowego (application-test.properties)
@DataJpaTest
@ActiveProfiles("test")
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // TestEntityManager - narzedzie testowe do bezposredniej pracy z EntityManagerem
    // pozwala na flush() i clear() zeby wymusic synchronizacje z baza
    @Autowired
    private TestEntityManager entityManager;

    private Category category;

    // @BeforeEach - wykonuje sie przed KAZDYM testem
    // deleteAllInBatch() wykonuje natychmiastowy SQL DELETE (szybsze niz deleteAll)
    // flush + clear - wymusza synchronizacje z baza i czysci cache pierwszego poziomu
    @BeforeEach
    void setUp() {
        noteRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        entityManager.flush();
        entityManager.clear();

        category = categoryRepository.saveAndFlush(new Category("Uczelnia"));

        Note note1 = new Note("Projekt z baz danych", "Oddac do piatku");
        note1.setCategory(category);
        noteRepository.saveAndFlush(note1);

        Note note2 = new Note("Zakupy na weekend", "Mleko, chleb");
        noteRepository.saveAndFlush(note2);

        Note note3 = new Note("Notatka z baz danych", "SQL, JPA, Hibernate");
        note3.setCategory(category);
        noteRepository.saveAndFlush(note3);
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
        Note saved = noteRepository.saveAndFlush(new Note("Testowa", "Tresc"));

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
        Note saved = noteRepository.saveAndFlush(new Note("Stary tytul", "Stara tresc"));
        saved.setTitle("Nowy tytul");
        Note updated = noteRepository.saveAndFlush(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Nowy tytul", updated.getTitle());
    }

    // Test: usuwanie notatki z bazy
    @Test
    void delete_shouldRemoveNote() {
        Note saved = noteRepository.saveAndFlush(new Note("Do usuniecia", "test"));
        Long id = saved.getId();

        noteRepository.delete(saved);
        noteRepository.flush();

        assertFalse(noteRepository.findById(id).isPresent());
    }

    // Test: notatka z przypisana kategoria
    @Test
    void save_shouldPersistNoteWithCategory() {
        Note note = new Note("Z kategoria", "Tresc");
        note.setCategory(category);
        Note saved = noteRepository.saveAndFlush(note);

        entityManager.clear();
        Note found = noteRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(found.getCategory());
        assertEquals("Uczelnia", found.getCategory().getName());
    }

    // Test: notatka bez kategorii (category = null)
    @Test
    void save_shouldPersistNoteWithoutCategory() {
        Note note = new Note("Bez kategorii", "Tresc");
        Note saved = noteRepository.saveAndFlush(note);

        entityManager.clear();
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
