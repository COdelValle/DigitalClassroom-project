-- Data.sql: Datos iniciales para Assessment_Manager
-- Se ejecuta automáticamente en el startup

-- Limpiar datos existentes (para reinicialización)
DELETE FROM grade;
DELETE FROM assessment;

-- Insertar Evaluaciones de ejemplo
INSERT INTO assessment (id, title, course_id, exam_date, is_graded) VALUES
(1, 'Prueba de Matemática - Unidad 1', 1, '2026-05-20', false),
(2, 'Prueba de Lenguaje - Ortografía', 1, '2026-05-22', false),
(3, 'Examen de Historia', 2, '2026-06-10', false),
(4, 'Quiz de Inglés', 3, '2026-05-25', false),
(5, 'Trabajo Práctico de Biología', 4, '2026-05-28', false);

-- Insertar Calificaciones de ejemplo
INSERT INTO grade (id, student_id, score, assessment_id, registration_date) VALUES
(1, 101, 5.5, 1, NOW()),
(2, 102, 6.0, 1, NOW()),
(3, 103, 7.0, 1, NOW()),
(4, 101, 6.5, 2, NOW()),
(5, 102, 5.8, 2, NOW()),
(6, 104, 6.8, 3, NOW()),
(7, 105, 5.2, 4, NOW()),
(8, 101, 7.0, 5, NOW());

-- Actualizar estado is_graded para evaluaciones que ya tienen calificaciones
UPDATE assessment SET is_graded = true WHERE id IN (1, 2, 3, 4, 5);
