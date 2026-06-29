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
    birth_date TIMESTAMP NOT NULL,
    allergies VARCHAR(1000),
    legal_representatives CLOB,
    CONSTRAINT unique_rut UNIQUE (rut)
);

-- Índices
CREATE INDEX idx_rut ON student(rut);
