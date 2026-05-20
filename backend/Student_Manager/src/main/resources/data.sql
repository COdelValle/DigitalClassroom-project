-- Data.sql: Datos iniciales para Student_Manager
-- Se ejecuta automáticamente en el startup

-- Limpiar datos existentes
DELETE FROM student;

-- Insertar Estudiantes de ejemplo
INSERT INTO student (id, rut, first_name, middle_name, last_name, birth_date, allergies, emergency_contact, emergency_phone, health_condition, medication_name, medication_frequency, notes, created_at, updated_at) VALUES
(101, '19.123.456-K', 'Juan', 'Carlos', 'González López', '2010-03-15', 'Ninguna', 'María González', '+56912345678', 'Sin problemas', NULL, NULL, 'Estudiante regular', NOW(), NOW()),
(102, '19.234.567-L', 'María', 'Elena', 'Pérez Martínez', '2010-05-22', 'Alergia al maní', 'Roberto Pérez', '+56923456789', 'Alergia leve', 'Antihistamínico', 'Según sea necesario', 'Llevar medicamento en mochilas', NOW(), NOW()),
(103, '19.345.678-M', 'Carlos', 'Alberto', 'Rodríguez Silva', '2010-07-10', 'Ninguna', 'Ana Rodríguez', '+56934567890', 'Sin problemas', NULL, NULL, 'Alumno destacado', NOW(), NOW()),
(104, '19.456.789-N', 'Laura', 'Francisca', 'Hernández López', '2010-09-03', 'Alergia al gluten', 'Francisco Hernández', '+56945678901', 'Celíaca', 'Dieta especial', 'Siempre', 'Requiere almuerzo sin gluten', NOW(), NOW()),
(105, '19.567.890-O', 'Diego', NULL, 'Díaz Soto', '2010-11-28', 'Ninguna', 'Patricia Díaz', '+56956789012', 'Sin problemas', NULL, NULL, 'Estudiante regular', NOW(), NOW());
