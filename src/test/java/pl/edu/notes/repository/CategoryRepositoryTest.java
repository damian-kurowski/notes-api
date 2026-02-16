package pl.edu.notes.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import pl.edu.notes.model.Category;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest - testuje TYLKO warstwe JPA z baza H2 in-memory
// @ActiveProfiles("test") - uzywa application-test.properties zamiast application.properties
// dzieki temu testy maja osobna baze danych i nie laduja data.sql
@DataJpaTest
@ActiveProfiles("test")
// @TestMethodOrder - testy wykonuja sie w kolejnosci nazw metod (przewidywalnosc)
@TestMethodOrder(MethodOrderer.MethodName.class)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    // @BeforeAll - wykonuje sie RAZ przed wszystkimi testami (nie przed kazdym)
    // static - wymagane przez JUnit 5 dla @BeforeAll
    // tworzy dane testowe tylko raz - unika problemu z unikatowoscia nazw
    @BeforeAll
    static void setUpAll(@Autowired CategoryRepository categoryRepository) {
        categoryRepository.save(new Category("Osobiste"));
        categoryRepository.save(new Category("Uczelnia"));
        categoryRepository.save(new Category("Praca"));
    }

    // @AfterEach - wykonuje sie PO kazdym tescie
    // cofa zmiany wprowadzone przez test (np. dodane lub usuniete kategorie)
    // przywraca baze do stanu poczatkowego (3 kategorie z @BeforeAll)
    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
        categoryRepository.save(new Category("Osobiste"));
        categoryRepository.save(new Category("Uczelnia"));
        categoryRepository.save(new Category("Praca"));
    }

    // Test: zapisanie nowej kategorii do bazy i sprawdzenie czy dostala id
    @Test
    @Rollback(false)
    void save_shouldPersistCategory() {
        Category category = new Category("Hobby");
        Category saved = categoryRepository.save(category);

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
        Category saved = categoryRepository.save(new Category("Testowa"));

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

    // Test: aktualizacja nazwy istniejacj kategorii
    @Test
    void save_shouldUpdateExistingCategory() {
        Category saved = categoryRepository.save(new Category("Stara"));
        saved.setName("Zmieniona");
        Category updated = categoryRepository.save(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Zmieniona", updated.getName());
    }

    // Test: usuwanie kategorii z bazy
    @Test
    void delete_shouldRemoveCategory() {
        Category saved = categoryRepository.save(new Category("Do usuniecia"));
        Long id = saved.getId();

        categoryRepository.delete(saved);

        assertFalse(categoryRepository.findById(id).isPresent());
    }

    // Test: count zwraca poprawna liczbe rekordow w bazie
    @Test
    void count_shouldReturnNumberOfCategories() {
        long count = categoryRepository.count();

        assertEquals(3, count);
    }
}
