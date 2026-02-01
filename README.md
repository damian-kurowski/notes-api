# Notes API

REST API do zarządzania notatkami, zbudowane w Spring Boot.

## Technologie

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- Maven

## Uruchomienie

```bash
mvn spring-boot:run
```

Aplikacja uruchomi się na porcie `8080`.

## Endpointy API

| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/notes` | Pobierz wszystkie notatki |
| GET | `/api/notes/{id}` | Pobierz notatkę po ID |
| POST | `/api/notes` | Utwórz nową notatkę |
| PUT | `/api/notes/{id}` | Zaktualizuj notatkę |
| DELETE | `/api/notes/{id}` | Usuń notatkę |
| GET | `/api/notes/search?title={title}` | Szukaj notatek po tytule |

## Przykłady użycia

### Pobierz wszystkie notatki
```bash
curl http://localhost:8080/api/notes
```

### Utwórz notatkę
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"title": "Moja notatka", "content": "Treść notatki"}'
```

### Zaktualizuj notatkę
```bash
curl -X PUT http://localhost:8080/api/notes/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Nowy tytuł", "content": "Nowa treść"}'
```

### Usuń notatkę
```bash
curl -X DELETE http://localhost:8080/api/notes/1
```

## Konsola H2

Dostępna pod adresem: http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:notesdb`
- Username: `sa`
- Password: (puste)

## Struktura projektu

```
src/main/java/pl/edu/notes/
├── NotesApplication.java       # Klasa główna
├── controller/
│   └── NoteController.java     # REST Controller
├── model/
│   └── Note.java               # Encja JPA
└── repository/
    └── NoteRepository.java     # Repozytorium JPA
```
