package pl.edu.notes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa kategorii nie może być pusta")
    @Size(max = 50, message = "Nazwa kategorii może mieć maksymalnie 50 znaków")
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    // @OneToMany - jedna kategoria moze miec wiele notatek
    // mappedBy = "category" - wskazuje pole w klasie Note ktore jest wlascicielem relacji
    // cascade = CascadeType.ALL - operacje na kategorii propaguja sie na notatki (np. usun kategorie = usun jej notatki)
    // orphanRemoval = true - notatka bez kategorii zostanie usunieta
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("category") // zapobiega nieskonczonej petli JSON
    private List<Note> notes;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
