-- Init Database Script - Digital Classroom
-- Este script crea las 3 bases de datos con sus tablas y datos iniciales

-- ==================== ASSESSMENT_MANAGER_DB ====================
CREATE DATABASE IF NOT EXISTS assessment_manager_db;
USE assessment_manager_db;

-- Tabla Assessment (Evaluaciones)
CREATE TABLE IF NOT EXISTS assessment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    course_id BIGINT NOT NULL,
    exam_date DATE,
    is_graded BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Grade (Calificaciones)
CREATE TABLE IF NOT EXISTS grade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    score DOUBLE NOT NULL CHECK (score >= 1 AND score <= 7),
    assessment_id BIGINT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assessment_id) REFERENCES assessment(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos iniciales Assessment
INSERT INTO assessment (id, title, course_id, exam_date, is_graded) VALUES
(1, 'Prueba de Matemática - Unidad 1', 1, '2026-05-20', true),
(2, 'Prueba de Lenguaje - Ortografía', 1, '2026-05-22', true),
(3, 'Examen de Historia', 2, '2026-06-10', true),
(4, 'Quiz de Inglés', 3, '2026-05-25', true),
(5, 'Trabajo Práctico de Biología', 4, '2026-05-28', false);

-- Datos iniciales Grade
INSERT INTO grade (id, student_id, score, assessment_id, registration_date) VALUES
(1, 101, 5.5, 1, NOW()),
(2, 102, 6.0, 1, NOW()),
(3, 103, 7.0, 1, NOW()),
(4, 101, 6.5, 2, NOW()),
(5, 102, 5.8, 2, NOW()),
(6, 104, 6.8, 3, NOW()),
(7, 105, 5.2, 4, NOW()),
(8, 101, 7.0, 5, NOW());

-- ==================== STUDENT_MANAGER_DB ====================
CREATE DATABASE IF NOT EXISTS student_manager_db;
USE student_manager_db;

-- Tabla Student (Estudiantes)
CREATE TABLE IF NOT EXISTS student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut VARCHAR(12) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    allergies VARCHAR(255),
    emergency_contact VARCHAR(150),
    emergency_phone VARCHAR(20),
    health_condition VARCHAR(255),
    medication_name VARCHAR(100),
    medication_frequency VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos iniciales Student
INSERT INTO student (id, rut, first_name, middle_name, last_name, birth_date, allergies, emergency_contact, emergency_phone, health_condition, medication_name, medication_frequency, notes) VALUES
(101, '19.123.456-K', 'Juan', 'Carlos', 'González López', '2010-03-15', 'Ninguna', 'María González', '+56912345678', 'Sin problemas', NULL, NULL, 'Estudiante regular'),
(102, '19.234.567-L', 'María', 'Elena', 'Pérez Martínez', '2010-05-22', 'Alergia al maní', 'Roberto Pérez', '+56923456789', 'Alergia leve', 'Antihistamínico', 'Según sea necesario', 'Llevar medicamento'),
(103, '19.345.678-M', 'Carlos', 'Alberto', 'Rodríguez Silva', '2010-07-10', 'Ninguna', 'Ana Rodríguez', '+56934567890', 'Sin problemas', NULL, NULL, 'Alumno destacado'),
(104, '19.456.789-N', 'Laura', 'Francisca', 'Hernández López', '2010-09-03', 'Alergia al gluten', 'Francisco Hernández', '+56945678901', 'Celíaca', 'Dieta especial', 'Siempre', 'Almuerzo sin gluten'),
(105, '19.567.890-O', 'Diego', NULL, 'Díaz Soto', '2010-11-28', 'Ninguna', 'Patricia Díaz', '+56956789012', 'Sin problemas', NULL, NULL, 'Estudiante regular');

-- ==================== CLASSROOM_MANAGER_DB ====================
CREATE DATABASE IF NOT EXISTS classroom_manager_db;
USE classroom_manager_db;

-- Tabla Subject (Asignaturas)
CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    area VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Classroom (Salas)
CREATE TABLE IF NOT EXISTS classroom (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    school_year INT NOT NULL,
    capacity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Course (Cursos)
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    classroom_id BIGINT NOT NULL,
    school_year INT NOT NULL,
    semester VARCHAR(10) NOT NULL,
    teacher_name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (classroom_id) REFERENCES classroom(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos iniciales Subject
INSERT INTO subjects (id, name, area, is_active) VALUES
(1, 'Matemática', 'STEM', true),
(2, 'Lenguaje y Literatura', 'Humanidades', true),
(3, 'Inglés', 'Idiomas', true),
(4, 'Biología', 'Ciencias Naturales', true),
(5, 'Historia', 'Ciencias Sociales', true),
(6, 'Educación Física', 'Deportes', true);

-- Datos iniciales Classroom
INSERT INTO classroom (id, code, name, school_year, capacity) VALUES
(1, 'SALA-101', '7°A - Matemática', 2026, 30),
(2, 'SALA-102', '7°B - Lenguaje', 2026, 28),
(3, 'SALA-103', '8°A - Ciencias', 2026, 32),
(4, 'SALA-104', '8°B - Historia', 2026, 29);

-- Datos iniciales Course
INSERT INTO courses (id, subject_id, classroom_id, school_year, semester, teacher_name) VALUES
(1, 1, 1, 2026, '1', 'Juan García Pérez'),
(2, 2, 2, 2026, '1', 'Elena Martínez López'),
(3, 4, 3, 2026, '1', 'Dr. Roberto Silva'),
(4, 5, 4, 2026, '1', 'Profesora María Rodríguez'),
(5, 3, 1, 2026, '1', 'Daniel Thompson'),
(6, 6, 2, 2026, '1', 'Coach Alejandro Ruiz');
