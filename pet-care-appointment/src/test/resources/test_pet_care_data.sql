-- Script to populate the Pet Care Database


-- Insert data into roles
INSERT INTO role (name) VALUES ('ADMIN'), ('PATIENT'), ('VETERINARIAN');

-- Insert data into users
-- Admin
INSERT INTO "user" (first_name, last_name, gender, mobile, email, password, user_type, is_enabled)
VALUES ('Admin', 'User', 'Male', '1234567890', 'admin@petcare.com', 'admin123', 'ADMIN', TRUE);
INSERT INTO admin (admin_id) VALUES (1);

-- Patients
INSERT INTO "user" (first_name, last_name, gender, mobile, email, password, user_type, is_enabled)
VALUES
    ('Алексей', 'Петров', 'Male', '89121234567', 'alexey@gmail.com', 'pass123', 'PATIENT', TRUE),
    ('Мария', 'Сидорова', 'Female', '89121111111', 'maria@gmail.com', 'pass123', 'PATIENT', TRUE),
    ('Игорь', 'Кузнецов', 'Male', '89122222222', 'igor@gmail.com', 'pass123', 'PATIENT', TRUE),
    ('Ольга', 'Иванова', 'Female', '89123333333', 'olga@gmail.com', 'pass123', 'PATIENT', TRUE),
    ('Анна', 'Смирнова', 'Female', '89124444444', 'anna@gmail.com', 'pass123', 'PATIENT', TRUE);

INSERT INTO patient (patient_id) VALUES (2), (3), (4), (5), (6);

-- Veterinarians
INSERT INTO "user" (first_name, last_name, gender, mobile, email, password, user_type, is_enabled)
VALUES
    ('Дмитрий', 'Сергеев', 'Male', '89125555555', 'dmitry@gmail.com', 'pass123', 'VETERINARIAN', TRUE),
    ('Наталья', 'Федорова', 'Female', '89126666666', 'natalia@gmail.com', 'pass123', 'VETERINARIAN', TRUE),
    ('Константин', 'Лебедев', 'Male', '89127777777', 'constantine@gmail.com', 'pass123', 'VETERINARIAN', TRUE),
    ('Валерия', 'Павлова', 'Female', '89128888888', 'valeria@gmail.com', 'pass123', 'VETERINARIAN', TRUE),
    ('Сергей', 'Григорьев', 'Male', '89129999999', 'sergey@gmail.com', 'pass123', 'VETERINARIAN', TRUE);

INSERT INTO veterinarian (veterinarian_id, specialization) VALUES
                                                               (7, 'Терапия'), (8, 'Терапия'), (9, 'Хирургия'), (10, 'Хирургия'), (11, 'Диагностика');

-- Assign roles
INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 2), (3, 2), (4, 2), (5, 2), (6, 2),
    (7, 3), (8, 3), (9, 3), (10, 3), (11, 3);


-- Reviews
INSERT INTO review (stars, reviewer_id, veterinarian_id, feedback)
VALUES
    (5, 2, 7, 'Отличный специалист! Очень доволен.'),
    (4, 3, 7, 'Хорошая консультация, но пришлось подождать.'),
    (5, 4, 8, 'Очень внимательная и профессиональная.'),
    (4, 5, 9, 'Хорошая работа, но клиника могла бы быть удобнее.'),
    (3, 6, 10, 'Врач компетентен, но общение оставляет желать лучшего.');

-- Appointments
INSERT INTO appointment (appointment_date, appointment_time, status, reason, sender, recipient)
VALUES
    ('2024-12-01', '10:00:00', 'COMPLETED', 'Плановый осмотр', 2, 7),
    ('2024-12-06', '11:00:00', 'CANCELLED', 'Вакцинация', 3, 7),
    ('2024-12-13', '12:00:00', 'ON_GOING', 'Лечение лапы', 4, 8),
    ('2024-12-15', '13:00:00', 'APPROVED', 'Удаление зубов', 5, 9),
    ('2024-12-20', '14:00:00', 'PENDING', 'УЗИ', 6, 10),
    ('2024-12-25', '15:00:00', 'WAITING_FOR_APPROVAL', 'Анализы', 2, 8),
    ('2024-12-30', '16:00:00', 'NOT_APPROVED', 'Обработка раны', 3, 10),
    ('2025-01-05', '17:00:00', 'UP_COMING', 'Чистка зубов', 4, 9);

-- Pets
INSERT INTO pet (name, breed, color, age, appointment_id)
VALUES
    ('Барсик', 'Британская короткошерстная', 'Серый', 3, 1),
    ('Мурка', 'Мейн-кун', 'Черный', 2, 2),
    ('Шарик', 'Лабрадор', 'Бежевый', 4, 3),
    ('Белка', 'Чихуахуа', 'Белый', 5, 4),
    ('Рыжик', 'Шотландская вислоухая', 'Рыжий', 3, 5),
    ('Дружок', 'Дворняжка', 'Коричневый', 6, 6),
    ('Тигр', 'Сибирская', 'Полосатый', 4, 7),
    ('Снежинка', 'Самоед', 'Белый', 5, 8);

-- Veterinarian Biographies
INSERT INTO vet_biography (biography, veterinarian_id)
VALUES
    ('Дмитрий Сергеев специализируется на терапии. Имеет 10 лет опыта работы с домашними животными.', 7),
    ('Наталья Федорова является экспертом в терапии. Особое внимание уделяет психическому состоянию животных.', 8),
    ('Константин Лебедев - опытный хирург с более чем 8-летней практикой.', 9),
    ('Валерия Павлова проводит сложные хирургические операции. Опыт работы более 7 лет.', 10),
    ('Сергей Григорьев специализируется на диагностике и лабораторных исследованиях.', 11);

