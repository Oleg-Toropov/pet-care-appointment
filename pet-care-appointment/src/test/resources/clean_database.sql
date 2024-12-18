-- Disable referential integrity to avoid errors while clearing data
SET REFERENTIAL_INTEGRITY FALSE;

-- Clear data from all tables
TRUNCATE TABLE user_roles;
TRUNCATE TABLE review;
TRUNCATE TABLE appointment;
TRUNCATE TABLE pet;
TRUNCATE TABLE vet_biography;
TRUNCATE TABLE veterinarian;
TRUNCATE TABLE patient;
TRUNCATE TABLE admin;
TRUNCATE TABLE role;
TRUNCATE TABLE "user";

-- Enable referential integrity back
SET REFERENTIAL_INTEGRITY TRUE;

-- Optionally: Reset auto-increment values for primary key columns (if necessary)
ALTER TABLE role ALTER COLUMN id RESTART WITH 1;
ALTER TABLE "user" ALTER COLUMN id RESTART WITH 1;
ALTER TABLE admin ALTER COLUMN admin_id RESTART WITH 1;
ALTER TABLE patient ALTER COLUMN patient_id RESTART WITH 1;
ALTER TABLE veterinarian ALTER COLUMN veterinarian_id RESTART WITH 1;
ALTER TABLE pet ALTER COLUMN id RESTART WITH 1;
ALTER TABLE appointment ALTER COLUMN id RESTART WITH 1;
ALTER TABLE review ALTER COLUMN id RESTART WITH 1;
ALTER TABLE vet_biography ALTER COLUMN id RESTART WITH 1;

