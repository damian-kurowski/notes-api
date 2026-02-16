INSERT INTO categories (name) VALUES
('Osobiste'),
('Uczelnia'),
('Praca');

INSERT INTO notes (title, content, category_id, created_at, updated_at) VALUES
('Zakupy', 'mleko, chleb, jajka', 1, NOW(), NOW()),
('Do zrobienia', 'oddac projekt z baz danych', 2, NOW(), NOW()),
('Notatka z wykladu', 'JPA - Java Persistence API, ORM dla Javy', 2, NOW(), NOW());
