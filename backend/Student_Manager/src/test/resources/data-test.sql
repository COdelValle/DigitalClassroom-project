-- Datos iniciales para pruebas (vacío, cada test configura sus datos)
-- Este archivo es ejecutado por Spring después de schema-h2.sql
-- Los tests usan @BeforeEach y @Transactional para limpiar datos
-- Agregar una instrucción válida para que Spring no falle por script vacío
SELECT 1;
