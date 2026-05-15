# Digital Classroom API - Postman Collection Guide

## Descripción General

Esta colección de Postman proporciona un conjunto completo de pruebas para todos los microservicios de la aplicación Digital Classroom. Incluye:

- **Student Manager**: Gestión de estudiantes (CRUD)
- **Classroom Manager**: Gestión de aulas, asignaturas y cursos
- **Assessment Manager**: Gestión de evaluaciones y calificaciones

## Estructura de la Colección

### 1. Variables Globales

La colección incluye las siguientes variables que puedes personalizar según tu entorno:

| Variable | Valor por Defecto       | Descripción |
|----------|-------------------------|-------------|
| `base_url` | `http://localhost:8080` | URL base del servidor |
| `student_manager_url` | `http://localhost:8081` | URL del Student Manager |
| `classroom_manager_url` | `http://localhost:8084` | URL del Classroom Manager |
| `assessment_manager_url` | `http://localhost:8083` | URL del Assessment Manager |
| `student_id` | `1`                     | ID del estudiante para pruebas |
| `classroom_id` | `1`                     | ID del aula para pruebas |
| `subject_id` | `1`                     | ID de la asignatura para pruebas |
| `course_id` | `1`                     | ID del curso para pruebas |
| `assessment_id` | `1`                     | ID de la evaluación para pruebas |
| `grade_id` | `1`                     | ID de la calificación para pruebas |

### 2. Configuración Inicial en Postman

#### Opción 1: Importar la Colección

1. Abre Postman
2. Haz clic en "File" → "Import"
3. Selecciona el archivo `DigitalClassroom-API-Collection.postman_collection.json`
4. La colección se importará automáticamente

#### Opción 2: Ajustar Variables

1. Abre Postman
2. Ve a "Environments" → "Manage Environments"
3. Crea un nuevo environment llamado "Digital Classroom"
4. Establece las variables según tu configuración local

## Pruebas Organizadas por Servicio

### 1. Student Manager (Puerto 8081)

#### Endpoints Disponibles: POST /api/v1/students
Crea un nuevo estudiante

**Escenarios:**
- ✅ Crear estudiante básico
- ✅ Crear con alergias
- ✅ Crear con múltiples representantes
- ❌ Crear con RUT inválido
- ❌ Crear sin alergias

**GET /api/v1/students**
Obtiene lista simplificada de estudiantes

**GET /api/v1/students/{id}/profile**
Obtiene vista simplificada del estudiante (para profesores)

**GET /api/v1/students/{id}/full**
Obtiene detalle completo del estudiante (para administradores)

**GET /api/v1/students/{id}/exists**
Verifica si un estudiante existe

**PUT /api/v1/students/{id}**
Actualiza información del estudiante

**DELETE /api/v1/students/{id}**
Elimina un estudiante

### 2. Classroom Manager (Puerto 8082)

#### Classrooms (/api/v1/classroom)

- **POST**: Crear aula
- **GET /{id}**: Obtener aula por ID
- **GET /search**: Buscar por nombre y/o año
- **PUT /{id}**: Actualizar aula
- **DELETE /{id}**: Eliminar aula

#### Subjects (/api/v1/subjects)

- **POST**: Crear asignatura
- **GET /{id}**: Obtener asignatura por ID
- **GET /search**: Buscar por nombre y/o área
- **PUT /{id}**: Actualizar asignatura
- **DELETE /{id}**: Eliminar asignatura

#### Courses (/api/v1/courses)

- **POST**: Crear curso (asignatura por aula)
- **GET /{id}**: Obtener curso por ID
- **GET /search**: Buscar por aula, profesor, semestre
- **GET /{id}/exists**: Verificar existencia
- **PUT /{id}**: Actualizar curso
- **DELETE /{id}**: Eliminar curso

### 3. Assessment Manager (Puerto 8083)

#### Assessments (/api/v1/assessments)

- **POST**: Crear evaluación
- **GET /{id}**: Obtener evaluación por ID
- **GET /search**: Buscar por curso, título, fecha
- **PUT /{id}**: Actualizar evaluación
- **DELETE /{id}**: Eliminar evaluación

#### Grades (/api/v1/grades)

- **POST**: Registrar calificación
- **GET /{id}**: Obtener calificación por ID
- **GET /search**: Buscar por estudiante, rango de puntuación, fecha
- **PUT /{id}**: Actualizar calificación
- **DELETE /{id}**: Eliminar calificación

## Flujo Recomendado de Pruebas

Para un testing completo y correcto, sigue este orden:

```
1. Student Manager
   ├─ Crear estudiante(s)
   ├─ Listar estudiantes
   ├─ Ver perfil
   ├─ Ver detalles completos
   └─ Actualizar/Eliminar

2. Classroom Manager
   ├─ Crear aula(s)
   ├─ Crear asignatura(s)
   ├─ Crear curso(s) (asignatura + aula)
   └─ Realizar búsquedas

3. Assessment Manager
   ├─ Crear evaluación(es)
   ├─ Crear calificación(es)
   └─ Realizar búsquedas

4. Error Scenarios
   └─ Probar casos de error
```

## Escenarios de Prueba

### Happy Path (Flujo Normal)

1. **Crear estudiante** → GET listado → GET detalle
2. **Crear aula** → Crear asignatura → Crear curso
3. **Crear evaluación** (linked a curso) → Crear calificación (linked a estudiante y evaluación)

### Error Scenarios

- RUT inválido o duplicado
- ID no existente
- Datos incompletos o inválidos
- Cursos no existentes al crear evaluación
- Estudiantes o evaluaciones no existentes al crear calificación

## Cómo Usar Scripts de Prueba

Algunos requests incluyen scripts automáticos que:

1. ✅ Capturan IDs generados
2. ✅ Los almacenan en variables de Postman
3. ✅ Permiten encadenamiento de requests

**Ejemplo:**
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set('student_id', jsonData.id);
}
```

Esto permite que al crear un estudiante, su ID se guarde automáticamente para usarlo en requests posteriores.

## Ejemplos de RUT Válidos para Pruebas

```
12.345.678-9
15.987.654-3
16.456.789-1
17.567.890-2
10.123.456-7
11.234.567-8
13.456.789-0
14.567.890-3
```

**Nota:** El RUT debe tener el formato XX.XXX.XXX-X y ser único en la base de datos.

## Parámetros de Búsqueda

### Students
- No tiene endpoint de búsqueda explícito (GET devuelve todos)

### Classrooms
- `name`: Nombre o parte del nombre del aula
- `year`: Año del aula

### Subjects
- `name`: Nombre de la asignatura
- `area`: Área (Ej: Ciencias Exactas, Humanidades)

### Courses
- `classroomId`: ID del aula
- `teacher`: Nombre del profesor
- `semester`: Semestre (1 o 2)

### Assessments
- `courseId`: ID del curso
- `title`: Título de la evaluación
- `examDate`: Fecha de examen (YYYY-MM-DD)

### Grades
- `studentId`: ID del estudiante
- `minScore`: Puntuación mínima
- `maxScore`: Puntuación máxima
- `date`: Fecha específica

## Troubleshooting

### Error: "Cannot find symbol method getRut()"
✅ Solucionado: Se corrigió la dependencia de Lombok en pom.xml

### Error: 404 en Cursos
✅ Verificar que el `courseId` está correctamente interpolado en la URL

### Error: "No static resource"
✅ Verificar que el path variable coincide con el parámetro del @PathVariable

### Servicios no disponibles
```
Student Manager: ¿Puerto 8081 activo?
Classroom Manager: ¿Puerto 8082 activo?
Assessment Manager: ¿Puerto 8083 activo?
```

## Comandos Útiles para Levantar Servicios

```bash
# En la raíz del backend
mvn clean install
mvn spring-boot:run -pl Student_Manager
mvn spring-boot:run -pl Classroom_Manager
mvn spring-boot:run -pl Assessment_Manager
```

## Mejoras Futuras

- [ ] Autenticación/Autorización
- [ ] Tests de performance
- [ ] Validaciones adicionales
- [ ] Casos de concurrencia
- [ ] Integration tests
- [ ] Datos de prueba más realistas

## Contacto y Soporte

Para reportar problemas o sugerencias sobre los endpoints, contacta al equipo de desarrollo.

---

**Última actualización:** 2026-05-14
**Versión de Colección:** 1.0.0

