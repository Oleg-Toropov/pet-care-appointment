-- Patients
INSERT INTO "user" (first_name, last_name, gender, mobile, email, password, user_type, is_enabled, created_at)
VALUES ('Алексей', 'Петров', 'Male', '89121234567', 'alexey@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', TRUE, '2024-01-02'),
       ('Мария', 'Сидорова', 'Female', '89121111111', 'maria@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', TRUE, '2024-01-03'),
       ('Игорь', 'Кузнецов', 'Male', '89122222222', 'igor@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', TRUE, '2024-01-04'),
       ('Ольга', 'Иванова', 'Female', '89123333333', 'olga@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', TRUE, '2024-01-05'),
       ('Анна', 'Смирнова', 'Female', '89124444444', 'anna@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', FALSE, '2024-01-06');

INSERT INTO patient (patient_id)
VALUES (1),
       (2),
       (3),
       (4),
       (5);

-- Photo
INSERT INTO photo (id, file_type, file_name, urlPhoto)
VALUES
    (2, 'image/jpeg', 'fake1.jpg', 'urlTest_1'),
    (3, 'image/png', 'fake2.png', 'urlTest_2');

UPDATE "user" SET photo_id = 2 WHERE id = 2;
UPDATE "user" SET photo_id = 3 WHERE id = 3;