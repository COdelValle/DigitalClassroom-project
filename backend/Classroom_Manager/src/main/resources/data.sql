-- Data.sql: Datos iniciales para Classroom_Manager
-- Se ejecuta automáticamente en el startup

-- Limpiar datos existentes
DELETE FROM courses;
DELETE FROM classroom;
DELETE FROM subjects;

-- Insertar Asignaturas
INSERT INTO subjects (id, name, area, is_active) VALUES
(1, 'Matemática', 'STEM', true),
(2, 'Lenguaje y Literatura', 'Humanidades', true),
(3, 'Inglés', 'Idiomas', true),
(4, 'Biología', 'Ciencias Naturales', true),
(5, 'Historia', 'Ciencias Sociales', true),
(6, 'Educación Física', 'Deportes', true);

-- Insertar Salas (Classrooms)
INSERT INTO classroom (id, code, name, school_year, capacity) VALUES
(1, 'SALA-101', '7°A - Matemática', 2026, 30),
(2, 'SALA-102', '7°B - Lenguaje', 2026, 28),
(3, 'SALA-103', '8°A - Ciencias', 2026, 32),
(4, 'SALA-104', '8°B - Historia', 2026, 29);

-- Insertar Cursos (vinculando Asignaturas + Salas)
INSERT INTO courses (id, subject_id, classroom_id, school_year, semester, teacher_name) VALUES
(1, 1, 1, 2026, '1', 'Juan García Pérez'),
(2, 2, 2, 2026, '1', 'Elena Martínez López'),
(3, 4, 3, 2026, '1', 'Dr. Roberto Silva'),
(4, 5, 4, 2026, '1', 'Profesora María Rodríguez'),
(5, 3, 1, 2026, '1', 'Daniel Thompson'),
(6, 6, 2, 2026, '1', 'Coach Alejandro Ruiz');
