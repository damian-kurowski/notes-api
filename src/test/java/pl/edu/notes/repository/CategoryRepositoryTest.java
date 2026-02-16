package pl.edu.notes.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.edu.notes.model.Category;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest - testuje TYLKO warstwe JPA z baza H2 in-memory
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        categoryRepository.save(new Category("Osobiste"));
        categoryRepository.save(new Category("Uczelnia"));
        categoryRepository.save(new Category("Praca"));
    }

    // Test: zapisanie kategorii do bazy
    @Test
    void save_shouldPersistCategory() {
        Category category = new Category("Hobby");
        Category saved = categoryRepository.save(category);

        assertNotNull(saved.getId());
        assertEquals("Hobby", saved.getName());
    }

    // Test: pobranie wszystkich kategorii
    @Test
    void findAll_shouldReturnAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        assertEquals(3, categories.size());
    }

    // Test: pobranie kategorii po id
    @Test
    void findById_shouldReturnCategory_whenExists() {
        Category saved = categoryRepository.save(new Category("Testowa"));

        Optional<Category> found = categoryRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Testowa", found.get().getName());
    }

    // Test: pobranie nieistniejacej kategorii
    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Category> found = categoryRepository.findById(9999L);

        assertFalse(found.isPresent());
    }

    // Test: aktualizacja nazwy kategorii
    @Test
    void save_shouldUpdateExistingCategory() {
        Category saved = categoryRepository.save(new Category("Stara"));
        saved.setName("Nowa");
        Category updated = categoryRepository.save(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Nowa", updated.getName());
    }

    // Test: usuwanie kategorii
    @Test
    void delete_shouldRemoveCategory() {
        Category saved = categoryRepository.save(new Category("Do usuniecia"));
        Long id = saved.getId();

        categoryRepository.delete(saved);

        assertFalse(categoryRepository.findById(id).isPresent());
    }

    // Test: count zwraca poprawna liczbe
    @Test
    void count_shouldReturnNumberOfCategories() {
        long count = categoryRepository.count();

        assertEquals(3, count);
    }
}
