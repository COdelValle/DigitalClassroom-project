-- Schema para H2 (Base de datos de prueba - Simple)
-- H2 soporta MySQL mode pero con limitaciones

DROP TABLE IF EXISTS student_representatives;
DROP TABLE IF EXISTS student;

-- Tabla: STUDENT
CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut VARCHAR(12) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    allergies CLOB,
    UNIQUE KEY unique_rut (rut)
);

-- Tabla: STUDENT_REPRESENTATIVES
CREATE TABLE student_representatives (
    student_id BIGINT NOT NULL,
    rut VARCHAR(12) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number CLOB,
    relationship VARCHAR(50) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_rut ON student(rut);
CREATE INDEX idx_student_id ON student_representatives(student_id);
