package pl.edu.notes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.notes.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
