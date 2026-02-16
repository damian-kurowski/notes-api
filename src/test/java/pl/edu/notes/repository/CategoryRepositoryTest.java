package pl.edu.notes.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.edu.notes.model.Category;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest - testuje TYLKO warstwe JPA z baza H2 in-memory
// @ActiveProfiles("test") - uzywa application-test.properties (osobna baza, bez data.sql)
@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    // TestEntityManager - narzedzie testowe Spring Data JPA
    // pozwala na flush() i clear() zeby wymusic zapis do bazy i wyczyscic cache
    @Autowired
    private TestEntityManager entityManager;

    // @BeforeEach - wykonuje sie przed KAZDYM testem
    // deleteAllInBatch() - natychmiastowy SQL DELETE (szybszy niz deleteAll)
    // flush + clear - synchronizacja z baza i czyszczenie cache Hibernate
    // dzieki temu kazdy test startuje z czysta baza i 3 kategoriami
    @BeforeEach
    void setUp() {
        categoryRepository.deleteAllInBatch();
        entityManager.flush();
        entityManager.clear();

        categoryRepository.saveAndFlush(new Category("Osobiste"));
        categoryRepository.saveAndFlush(new Category("Uczelnia"));
        categoryRepository.saveAndFlush(new Category("Praca"));
    }

    // Test: zapisanie nowej kategorii do bazy i sprawdzenie czy dostala id
    @Test
    void save_shouldPersistCategory() {
        Category category = new Category("Hobby");
        Category saved = categoryRepository.saveAndFlush(category);

        assertNotNull(saved.getId());
        assertEquals("Hobby", saved.getName());
    }

    // Test: pobranie wszystkich kategorii z bazy
    @Test
    void findAll_shouldReturnAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        assertEquals(3, categories.size());
    }

    // Test: pobranie kategorii po id - sprawdza czy Optional zawiera wynik
    @Test
    void findById_shouldReturnCategory_whenExists() {
        Category saved = categoryRepository.saveAndFlush(new Category("Testowa"));

        Optional<Category> found = categoryRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Testowa", found.get().getName());
    }

    // Test: pobranie nieistniejacej kategorii - sprawdza czy Optional jest pusty
    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Category> found = categoryRepository.findById(9999L);

        assertFalse(found.isPresent());
    }

    // Test: aktualizacja nazwy istniejacej kategorii
    @Test
    void save_shouldUpdateExistingCategory() {
        Category saved = categoryRepository.saveAndFlush(new Category("Stara"));
        saved.setName("Zmieniona");
        Category updated = categoryRepository.saveAndFlush(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Zmieniona", updated.getName());
    }

    // Test: usuwanie kategorii z bazy
    @Test
    void delete_shouldRemoveCategory() {
        Category saved = categoryRepository.saveAndFlush(new Category("Do usuniecia"));
        Long id = saved.getId();

        categoryRepository.delete(saved);
        categoryRepository.flush();

        assertFalse(categoryRepository.findById(id).isPresent());
    }

    // Test: count zwraca poprawna liczbe rekordow w bazie
    @Test
    void count_shouldReturnNumberOfCategories() {
        long count = categoryRepository.count();

        assertEquals(3, count);
    }
}
