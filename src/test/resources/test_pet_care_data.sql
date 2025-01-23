-- Script to populate the Pet Care Database

-- Insert data into roles
INSERT INTO role (name) VALUES ('ROLE_ADMIN'), ('ROLE_PATIENT'), ('ROLE_VET');

-- Insert data into users
-- Admin
INSERT INTO "user" (first_name, last_name, gender, mobile, email, password, user_type, is_enabled, created_at)
VALUES ('Admin', 'User', 'Male', '89127734565', 'admin@petcare.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'ADMIN', TRUE, '2024-01-01');
INSERT INTO admin (admin_id)
VALUES (1);

-- Patients
INSERT INTO "user" (first_name, last_name, gender, mobile, email, password, user_type, is_enabled, created_at)
VALUES ('Алексей', 'Петров', 'Male', '89121234567', 'alexey@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', TRUE, '2024-01-02'),
       ('Мария', 'Сидорова', 'Female', '89121111111', 'maria@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', TRUE, '2024-01-03'),
       ('Игорь', 'Кузнецов', 'Male', '89122222222', 'igor@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', TRUE, '2024-01-04'),
       ('Ольга', 'Иванова', 'Female', '89123333333', 'olga@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', FALSE, '2024-01-05'),
       ('Анна', 'Смирнова', 'Female', '89124444444', 'anna@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'PATIENT', FALSE, '2024-01-06');

INSERT INTO patient (patient_id)
VALUES (2),
       (3),
       (4),
       (5),
       (6);

-- Veterinarians
INSERT INTO "user" (first_name, last_name, gender, mobile, email, password, user_type, is_enabled, created_at)
VALUES ('Дмитрий', 'Сергеев', 'Male', '89125555555', 'dmitry@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'VET', TRUE, '2024-01-07'),
       ('Наталья', 'Федорова', 'Female', '89126666666', 'natalia@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'VET', TRUE, '2024-01-08'),
       ('Константин', 'Лебедев', 'Male', '89127777777', 'constantine@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'VET', TRUE, '2024-01-09'),
       ('Валерия', 'Павлова', 'Female', '89128888888', 'valeria@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'VET', TRUE, '2024-01-10'),
       ('Сергей', 'Григорьев', 'Male', '89129999999', 'sergey@gmail.com',
        '$2a$10$DkRHrrjcLKSz8biLdUGBjO8EUdJ9r9.kkgj6hgFZbGltQuhicA8KW', 'VET', FALSE, '2024-01-11');

INSERT INTO veterinarian (veterinarian_id, specialization)
VALUES (7, 'Терапевт'),
       (8, 'Терапевт'),
       (9, 'Хирург'),
       (10, 'Хирург'),
       (11, 'Диагност');

-- Assign roles
INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (4, 2),
       (5, 2),
       (6, 2),
       (7, 3),
       (8, 3),
       (9, 3),
       (10, 3),
       (11, 3);


-- Reviews
INSERT INTO review (stars, reviewer_id, veterinarian_id, feedback)
VALUES (5, 2, 8, 'Отличный специалист! Очень доволен.'),
       (5, 4, 8, 'Очень внимательная и профессиональная.'),
       (4, 2, 9, 'Хорошая работа, но клиника могла бы быть удобнее.'),
       (3, 6, 10, 'Врач компетентен, но общение оставляет желать лучшего.');

-- Appointments
INSERT INTO appointment (appointment_date, appointment_time, status, reason, sender, recipient)
VALUES ('2024-12-01', '10:00:00', 'COMPLETED', 'Плановый осмотр', 2, 7),
       ('2024-12-01', '15:00:00', 'COMPLETED', 'Плановый осмотр', 2, 8),
       ('2024-12-01', '15:00:00', 'COMPLETED', 'Плановый осмотр', 2, 9),
       ('2024-12-06', '11:00:00', 'CANCELLED', 'Вакцинация', 3, 7),
       ('2024-12-13', '12:00:00', 'ON_GOING', 'Лечение лапы', 4, 8),
       ('2024-12-15', '13:00:00', 'APPROVED', 'Удаление зубов', 5, 9),
       ('2024-12-20', '14:00:00', 'PENDING', 'УЗИ', 6, 10),
       ('2024-12-25', '15:00:00', 'WAITING_FOR_APPROVAL', 'Анализы', 2, 8),
       ('2024-12-30', '16:00:00', 'NOT_APPROVED', 'Обработка раны', 3, 10),
       ('2025-01-05', '17:00:00', 'UP_COMING', 'Чистка зубов', 4, 9),
       ('2025-01-05', '17:00:00', 'UP_COMING', 'Повторный осмотр', 2, 11),
       ('2025-01-05', '15:00:00', 'WAITING_FOR_APPROVAL', 'Анализы', 3, 7),
       ('2025-01-06', '15:00:00', 'WAITING_FOR_APPROVAL', 'Анализы', 3, 7),
       ('2025-01-07', '15:00:00', 'WAITING_FOR_APPROVAL', 'Анализы', 3, 7);

-- Pets
INSERT INTO pet (name, type, breed, color, age, appointment_id)
VALUES ('Барсик', 'Кошка','Сибирская', 'Черный', 3, 1),
       ('Мурка', 'Кошка', 'Мейн-кун', 'Черный', 2, 2),
       ('Шарик','Собака', 'Лабрадор', 'Белый', 4, 3),
       ('Белка', 'Собака', 'Чихуахуа', 'Белый', 5, 4),
       ('Рыжик', 'Кошка', 'Мейн-кун', 'Черный', 3, 5),
       ('Дружок', 'Собака', 'Дворняжка', 'Черный', 6, 6),
       ('Тигр', 'Кошка', 'Сибирская', 'Белый', 4, 7),
       ('Снежинка', 'Собака', 'Лабрадор', 'Белый', 5, 8),
       ('Мурка', 'Собака', 'Дворняжка', 'Черный', 3, 8);

-- Veterinarian Biographies
INSERT INTO vet_biography (id, biography, veterinarian_id)
VALUES (3, 'Дмитрий Сергеев специализируется на терапии. Имеет 10 лет опыта работы с домашними животными.', 7),
       (4, 'Наталья Федорова является экспертом в терапии. Особое внимание уделяет психическому состоянию животных.',
        8),
       (5, 'Валерия Павлова проводит сложные хирургические операции. Опыт работы более 7 лет.', 10);

