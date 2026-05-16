# 📚 Documentación de Endpoints - Digital Classroom Backend
Notas: Esta guia sirve más que nada para contextualizar, sin embargo los atributos que se devuelven no son los que corresponden, esta es una guia puramente provicional.
## Índice
1. [Descripción General](#descripción-general)
2. [Arquitectura Microservicios](#arquitectura-microservicios)
3. [Microservicio: Assessment Manager](#microservicio-assessment-manager)
4. [Microservicio: Classroom Manager](#microservicio-classroom-manager)
5. [Microservicio: Student Manager](#microservicio-student-manager)
6. [Microservicio: BFF Web](#microservicio-bff-web)
7. [Relaciones entre Endpoints](#relaciones-entre-endpoints)
8. [Casos de Uso Futuros para BFF](#casos-de-uso-futuros-para-bff)

---

## Descripción General

Este documento documenta todos los endpoints disponibles en la arquitectura de microservicios de **Digital Classroom**. El backend está dividido en 4 microservicios independientes:

- **Assessment Manager**: Gestión de evaluaciones y calificaciones
- **Classroom Manager**: Gestión de aulas, cursos y asignaturas
- **Student Manager**: Gestión de estudiantes
- **BFF Web**: Backend For Frontend (en desarrollo)

Cada microservicio expone una API REST bajo el prefijo `/api/v1/`.

---

## Arquitectura Microservicios

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React)                         │
└──────────────┬──────────────────────────────────┬────────────┘
               │                                  │
        ┌──────▼────────┐              ┌──────────▼──────┐
        │   BFF Web     │              │  Direct Calls   │
        │  (Agrega)     │              │  (Futuro)       │
        └──────┬────────┘              └─────────┬────────┘
               │                                  │
    ┌──────────┴─────────────────────────────────┴──────────┐
    │                                                         │
┌───▼────────────┐ ┌────────────────────┐ ┌──────────────┐ │
│ Assessment Mgr │ │ Classroom Manager  │ │ Student Mgr  │ │
│  (Eval/Grade)  │ │ (Aula/Asignatura)  │ │  (Alumnos)   │ │
└───────────────┘ └────────────────────┘ └──────────────┘ │
    │                     │                      │          │
    └─────────────────────┴──────────────────────┘          │
                                                             │
                        Base de Datos                        │
                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Microservicio: Assessment Manager

**Base URL**: `http://localhost:8083`  
**Rutas Base**:
- `/api/v1/assessments` - Encargos (Evaluaciones)
- `/api/v1/grades` - Calificaciones (Notas)

### Assessment Controller - Gestión de Encargos

Los encargos son evaluaciones o tareas asignadas a los estudiantes.

#### 1. Obtener Encargo por ID
```
GET /api/v1/assessments/{id}
```
**Descripción**: Obtiene los detalles de un encargo específico.

**Parámetros**:
- `id` (path, required): ID del encargo

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "title": "Prueba de Matemáticas",
  "courseId": 1,
  "examDate": "2024-05-20T14:00:00"
}
```

**Casos de Uso**:
- Obtener detalles de una evaluación antes de que un estudiante responda
- Mostrar información en vistas de detalle

---

#### 2. Buscar Encargos
```
GET /api/v1/assessments/search
```
**Descripción**: Busca encargos con filtros opcionales.

**Parámetros Query**:
- `courseId` (optional): Filtra por ID del curso
- `title` (optional): Filtra por título del encargo
- `examDate` (optional): Filtra por fecha de examen

**Ejemplos**:
```
GET /api/v1/assessments/search?courseId=1
GET /api/v1/assessments/search?title=Matemáticas
GET /api/v1/assessments/search?courseId=1&title=Prueba
```

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 1,
    "title": "Prueba de Matemáticas",
    "courseId": 1,
    "examDate": "2024-05-20T14:00:00"
  },
  {
    "id": 2,
    "title": "Tarea de Historia",
    "courseId": 2,
    "examDate": "2024-05-21T14:00:00"
  }
]
```

**Casos de Uso**:
- Listar evaluaciones de un curso específico
- Filtrar evaluaciones por profesor
- Buscar evaluaciones próximas por estudiante

---

#### 3. Crear Encargo
```
POST /api/v1/assessments
Content-Type: application/json
```
**Descripción**: Crea un nuevo encargo.

**Body**:
```json
{
  "title": "Examen Final",
  "description": "Evaluación trimestral",
  "courseId": 1,
  "examDate": "2024-06-15T14:00:00",
  "totalPoints": 100
}
```

**Respuesta Exitosa (201)**:
```json
{
  "id": 5,
  "title": "Examen Final",
  "description": "Evaluación trimestral",
  "courseId": 1,
  "examDate": "2024-06-15T14:00:00",
  "totalPoints": 100
}
```

**Header Location**: `Location: /api/v1/assessments/5`

**Casos de Uso**:
- Crear una nueva evaluación o tarea
- Planificar evaluaciones para un curso

---

#### 4. Actualizar Encargo
```
PUT /api/v1/assessments/{id}
Content-Type: application/json
```
**Descripción**: Actualiza un encargo existente.

**Parámetros**:
- `id` (path, required): ID del encargo

**Body** (campos opcionales):
```json
{
  "title": "Examen Final Modificado",
  "examDate": "2024-06-16T14:00:00"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 5,
  "title": "Examen Final Modificado",
  "examDate": "2024-06-16T14:00:00"
}
```

**Casos de Uso**:
- Postponer una evaluación
- Cambiar el título o descripción de un encargo
- Modificar el puntaje total

---

#### 5. Eliminar Encargo
```
DELETE /api/v1/assessments/{id}
```
**Descripción**: Elimina un encargo específico.

**Parámetros**:
- `id` (path, required): ID del encargo

**Respuesta Exitosa (204)**: Sin contenido

**Casos de Uso**:
- Cancelar una evaluación programada
- Eliminar evaluaciones duplicadas

---

### Grade Controller - Gestión de Calificaciones

Las calificaciones/notas son los puntajes que obtienen los estudiantes en las evaluaciones.

#### 1. Obtener Calificación por ID
```
GET /api/v1/grades/{id}
```
**Descripción**: Obtiene los detalles de una calificación específica.

**Parámetros**:
- `id` (path, required): ID de la calificación

**Respuesta Exitosa (200)**:
```json
{
  "id": 10,
  "studentId": 1,
  "assessmentId": 1,
  "score": 85.5,
  "feedback": "Excelente trabajo",
  "gradedDate": "2024-05-21T10:30:00"
}
```

**Casos de Uso**:
- Consultar una calificación específica
- Verificar feedback de una tarea

---

#### 2. Buscar Calificaciones
```
GET /api/v1/grades/search
```
**Descripción**: Busca calificaciones con filtros opcionales.

**Parámetros Query**:
- `studentId` (optional): Filtra por ID de estudiante
- `minScore` (optional): Puntaje mínimo
- `maxScore` (optional): Puntaje máximo
- `date` (optional): Filtra por fecha

**Ejemplos**:
```
GET /api/v1/grades/search?studentId=1
GET /api/v1/grades/search?minScore=70&maxScore=100
GET /api/v1/grades/search?studentId=1&minScore=80
```

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 10,
    "studentId": 1,
    "assessmentId": 1,
    "score": 85.5
  },
  {
    "id": 11,
    "studentId": 1,
    "assessmentId": 2,
    "score": 92.0
  }
]
```

**Casos de Uso**:
- Obtener todas las calificaciones de un estudiante
- Listar calificaciones de una evaluación específica
- Filtrar calificaciones por rango de notas

---

#### 3. Crear Calificación
```
POST /api/v1/grades
Content-Type: application/json
```
**Descripción**: Crea una nueva calificación (nota independiente).

**Body**:
```json
{
  "studentId": 1,
  "assessmentId": 1,
  "score": 85.5,
  "feedback": "Excelente trabajo",
  "gradedDate": "2024-05-21T10:30:00"
}
```

**Respuesta Exitosa (201)**:
```json
{
  "id": 10,
  "studentId": 1,
  "assessmentId": 1,
  "score": 85.5,
  "feedback": "Excelente trabajo",
  "gradedDate": "2024-05-21T10:30:00"
}
```

**Casos de Uso**:
- Registrar una calificación manual
- Crear notas independientes de evaluaciones
- Importar calificaciones desde otra fuente

---

#### 4. Actualizar Calificación
```
PUT /api/v1/grades/{id}
Content-Type: application/json
```
**Descripción**: Actualiza una calificación existente.

**Parámetros**:
- `id` (path, required): ID de la calificación

**Body** (campos opcionales):
```json
{
  "score": 90.0,
  "feedback": "Corrección realizada - Excelente trabajo"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 10,
  "score": 90.0,
  "feedback": "Corrección realizada - Excelente trabajo"
}
```

**Casos de Uso**:
- Corregir una calificación registrada
- Agregar feedback a una nota
- Modificar la evaluación después de revisión

---

#### 5. Eliminar Calificación
```
DELETE /api/v1/grades/{id}
```
**Descripción**: Elimina una calificación específica.

**Parámetros**:
- `id` (path, required): ID de la calificación

**Respuesta Exitosa (204)**: Sin contenido

**Casos de Uso**:
- Eliminar calificaciones erroneas
- Remover notas registradas por error

---

## Microservicio: Classroom Manager

**Base URL**: `http://localhost:8080`  
**Rutas Base**:
- `/api/v1/classroom` - Aulas (Cursos)
- `/api/v1/courses` - Relación Cursos-Asignaturas
- `/api/v1/subjects` - Asignaturas

### Classroom Controller - Gestión de Aulas/Cursos

#### 1. Obtener Aula por ID
```
GET /api/v1/classroom/{id}
```
**Descripción**: Obtiene los detalles de una aula (curso) específica.

**Parámetros**:
- `id` (path, required): ID del aula

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "name": "4to A",
  "year": 2024,
  "level": "Básico",
  "capacity": 30
}
```

**Casos de Uso**:
- Obtener información de un aula
- Verificar detalles de un curso

---

#### 2. Buscar Aulas
```
GET /api/v1/classroom/search
```
**Descripción**: Busca aulas con filtros opcionales.

**Parámetros Query**:
- `name` (optional): Nombres del aula (ej: "4to A")
- `year` (optional): Año del aula (ej: 2024)

**Ejemplos**:
```
GET /api/v1/classroom/search?year=2024
GET /api/v1/classroom/search?name=4to
```

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 1,
    "name": "4to A",
    "year": 2024
  },
  {
    "id": 2,
    "name": "4to B",
    "year": 2024
  }
]
```

**Casos de Uso**:
- Listar todas las aulas del año actual
- Buscar aulas por grado
- Filtrar aulas por año

---

#### 3. Crear Aula
```
POST /api/v1/classroom
Content-Type: application/json
```
**Descripción**: Crea una nueva aula.

**Body**:
```json
{
  "name": "5to A",
  "year": 2024,
  "level": "Intermedio",
  "capacity": 35
}
```

**Respuesta Exitosa (201)**:
```json
{
  "id": 3,
  "name": "5to A",
  "year": 2024,
  "level": "Intermedio",
  "capacity": 35
}
```

**Casos de Uso**:
- Crear un nuevo curso para el año escolar
- Registrar una nueva aula en el sistema

---

#### 4. Actualizar Aula
```
PUT /api/v1/classroom/{id}
Content-Type: application/json
```
**Descripción**: Actualiza una aula existente.

**Parámetros**:
- `id` (path, required): ID del aula

**Body** (campos opcionales):
```json
{
  "name": "5to A - Tarde",
  "capacity": 32
}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 3,
  "name": "5to A - Tarde",
  "capacity": 32
}
```

**Casos de Uso**:
- Cambiar nombre de aula
- Actualizar capacidad del aula
- Modificar información del curso

---

#### 5. Eliminar Aula
```
DELETE /api/v1/classroom/{id}
```
**Descripción**: Elimina una aula específica.

**Parámetros**:
- `id` (path, required): ID del aula

**Respuesta Exitosa (204)**: Sin contenido

**Casos de Uso**:
- Remover un aula del sistema
- Eliminar cursos descontinuados

---

### Course Controller - Gestión de Relación Cursos-Asignaturas

Representa la relación entre un aula (classroom) y una asignatura (subject) con un profesor asignado.

#### 1. Obtener Relación Curso-Asignatura
```
GET /api/v1/courses/{id}
```
**Descripción**: Obtiene los detalles de una relación curso-asignatura.

**Parámetros**:
- `id` (path, required): ID de la relación

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "classroomId": 1,
  "subjectId": 1,
  "teacher": "Prof. Juan García",
  "semester": "Primer Semestre",
  "startDate": "2024-02-01",
  "endDate": "2024-07-15"
}
```

**Casos de Uso**:
- Obtener detalles de una materia en un aula
- Identificar profesor de una asignatura
- Conocer período de enseñanza

---

#### 2. Buscar Relaciones Curso-Asignatura
```
GET /api/v1/courses/search
```
**Descripción**: Busca relaciones con filtros opcionales.

**Parámetros Query**:
- `classroomId` (optional): Filtra por aula
- `teacher` (optional): Filtra por profesor
- `semester` (optional): Filtra por semestre

**Ejemplos**:
```
GET /api/v1/courses/search?classroomId=1
GET /api/v1/courses/search?teacher=Prof. Juan García
GET /api/v1/courses/search?classroomId=1&semester=Primer Semestre
```

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 1,
    "classroomId": 1,
    "subjectId": 1,
    "teacher": "Prof. Juan García",
    "semester": "Primer Semestre"
  },
  {
    "id": 2,
    "classroomId": 1,
    "subjectId": 2,
    "teacher": "Prof. María López",
    "semester": "Primer Semestre"
  }
]
```

**Casos de Uso**:
- Listar todas las asignaturas de un aula
- Listar todas las asignaturas de un profesor
- Obtener materias de un semestre

---

#### 3. Crear Relación Curso-Asignatura
```
POST /api/v1/courses
Content-Type: application/json
```
**Descripción**: Crea una nueva relación curso-asignatura.

**Body**:
```json
{
  "classroomId": 1,
  "subjectId": 3,
  "teacher": "Prof. Carlos Rodríguez",
  "semester": "Primer Semestre",
  "startDate": "2024-02-01",
  "endDate": "2024-07-15"
}
```

**Respuesta Exitosa (201)**:
```json
{
  "id": 3,
  "classroomId": 1,
  "subjectId": 3,
  "teacher": "Prof. Carlos Rodríguez",
  "semester": "Primer Semestre"
}
```

**Casos de Uso**:
- Asignar una asignatura a un aula
- Designar profesor para una materia
- Crear plan académico de un curso

---

#### 4. Actualizar Relación Curso-Asignatura
```
PUT /api/v1/courses/{id}
Content-Type: application/json
```
**Descripción**: Actualiza una relación existente.

**Parámetros**:
- `id` (path, required): ID de la relación

**Body** (campos opcionales):
```json
{
  "teacher": "Prof. Carlos Martínez",
  "semester": "Segundo Semestre"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 3,
  "teacher": "Prof. Carlos Martínez",
  "semester": "Segundo Semestre"
}
```

**Casos de Uso**:
- Cambiar profesor de una asignatura
- Actualizar período del semestre
- Modificar información de la clase

---

#### 5. Eliminar Relación Curso-Asignatura
```
DELETE /api/v1/courses/{id}
```
**Descripción**: Elimina una relación específica.

**Parámetros**:
- `id` (path, required): ID de la relación

**Respuesta Exitosa (204)**: Sin contenido

**Casos de Uso**:
- Remover una asignatura de un aula
- Descontinuar una materia
- Corregir asignaciones incorrectas

---

#### 6. Verificar Existencia de Relación
```
GET /api/v1/courses/{id}/exists
```
**Descripción**: Verifica si una relación curso-asignatura existe.

**Parámetros**:
- `id` (path, required): ID de la relación

**Respuesta Exitosa (200)**:
```
true
```
o
```
false
```

**Casos de Uso**:
- Validar antes de crear evaluaciones
- Verificar si un profesor tiene asignada una materia

---

### Subject Controller - Gestión de Asignaturas

#### 1. Obtener Asignatura por ID
```
GET /api/v1/subjects/{id}
```
**Descripción**: Obtiene los detalles de una asignatura específica.

**Parámetros**:
- `id` (path, required): ID de la asignatura

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "name": "Matemáticas",
  "area": "Ciencias",
  "description": "Estudio de números, álgebra y geometría",
  "credits": 4
}
```

**Casos de Uso**:
- Obtener información de una materia
- Mostrar detalles de asignatura en UI

---

#### 2. Buscar Asignaturas
```
GET /api/v1/subjects/search
```
**Descripción**: Busca asignaturas con filtros opcionales.

**Parámetros Query**:
- `name` (optional): Filtra por nombre (ej: "Matemáticas")
- `area` (optional): Filtra por área (ej: "Ciencias", "Humanidades")

**Ejemplos**:
```
GET /api/v1/subjects/search?area=Ciencias
GET /api/v1/subjects/search?name=Matemática
```

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 1,
    "name": "Matemáticas",
    "area": "Ciencias"
  },
  {
    "id": 2,
    "name": "Física",
    "area": "Ciencias"
  }
]
```

**Casos de Uso**:
- Listar asignaturas por área de conocimiento
- Buscar una asignatura específica
- Filtrar disponibles para un currículo

---

#### 3. Crear Asignatura
```
POST /api/v1/subjects
Content-Type: application/json
```
**Descripción**: Crea una nueva asignatura.

**Body**:
```json
{
  "name": "Química",
  "area": "Ciencias",
  "description": "Estudio de elementos y reacciones químicas",
  "credits": 3
}
```

**Respuesta Exitosa (201)**:
```json
{
  "id": 5,
  "name": "Química",
  "area": "Ciencias",
  "description": "Estudio de elementos y reacciones químicas",
  "credits": 3
}
```

**Casos de Uso**:
- Crear nueva materia del currículo
- Agregar disciplina académica al catálogo

---

#### 4. Actualizar Asignatura
```
PUT /api/v1/subjects/{id}
Content-Type: application/json
```
**Descripción**: Actualiza una asignatura existente.

**Parámetros**:
- `id` (path, required): ID de la asignatura

**Body** (campos opcionales):
```json
{
  "name": "Química Avanzada",
  "credits": 4
}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 5,
  "name": "Química Avanzada",
  "credits": 4
}
```

**Casos de Uso**:
- Actualizar información de asignatura
- Cambiar créditos de un curso
- Modificar descripción o área

---

#### 5. Eliminar Asignatura
```
DELETE /api/v1/subjects/{id}
```
**Descripción**: Elimina una asignatura específica.

**Parámetros**:
- `id` (path, required): ID de la asignatura

**Respuesta Exitosa (204)**: Sin contenido

**Casos de Uso**:
- Remover asignatura del currículo
- Descontinuar una materia
- Limpiar errores de registro

---

## Microservicio: Student Manager

**Base URL**: `http://localhost:8080`  
**Ruta Base**: `/api/v1/students`

### Student Controller - Gestión de Estudiantes

#### 1. Crear Estudiante
```
POST /api/v1/students
Content-Type: application/json
```
**Descripción**: Crea un nuevo estudiante en el sistema.

**Body**:
```json
{
  "firstName": "Juan",
  "lastName": "García",
  "email": "juan.garcia@student.edu",
  "identificationNumber": "12345678",
  "birthDate": "2008-03-15",
  "enrollmentDate": "2024-02-01",
  "classroomId": 1
}
```

**Respuesta Exitosa (201)**:
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "García",
  "email": "juan.garcia@student.edu",
  "identificationNumber": "12345678"
}
```

**Casos de Uso**:
- Matricular un nuevo estudiante
- Importar estudiantes en masa
- Registrar estudiantes de nuevos ingresos

---

#### 2. Listar Estudiantes (Tabla Simplificada)
```
GET /api/v1/students
```
**Descripción**: Obtiene una lista simplificada de todos los estudiantes para tablas.

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 1,
    "firstName": "Juan",
    "lastName": "García",
    "email": "juan.garcia@student.edu"
  },
  {
    "id": 2,
    "firstName": "María",
    "lastName": "López",
    "email": "maria.lopez@student.edu"
  }
]
```

**Casos de Uso**:
- Listar estudiantes en tablas
- Mostrar dropdowns con estudiantes
- Exportar lista de estudiantes

---

#### 3. Obtener Perfil del Estudiante
```
GET /api/v1/students/{id}/profile
```
**Descripción**: Obtiene el perfil de estudiante (Vista Profesor).

**Parámetros**:
- `id` (path, required): ID del estudiante

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "García",
  "email": "juan.garcia@student.edu",
  "identificationNumber": "12345678",
  "birthDate": "2008-03-15",
  "enrollmentDate": "2024-02-01",
  "classroomId": 1,
  "currentGPA": 3.8
}
```

**Casos de Uso**:
- Vista profesor: Ver información básica del estudiante
- Histórico de desempeño
- Verificar estado del estudiante

---

#### 4. Obtener Detalle Completo del Estudiante
```
GET /api/v1/students/{id}/full
```
**Descripción**: Obtiene el detalle completo del estudiante (Vista Admin).

**Parámetros**:
- `id` (path, required): ID del estudiante

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "García",
  "email": "juan.garcia@student.edu",
  "identificationNumber": "12345678",
  "birthDate": "2008-03-15",
  "enrollmentDate": "2024-02-01",
  "classroomId": 1,
  "address": "Calle Principal 123",
  "phone": "+56912345678",
  "parentContact": "Juana García",
  "allGrades": [
    {
      "id": 1,
      "assessmentTitle": "Prueba Matemáticas",
      "score": 85.5
    }
  ],
  "enrollments": [
    {
      "courseId": 1,
      "subjectName": "Matemáticas",
      "teacher": "Prof. Juan"
    }
  ]
}
```

**Casos de Uso**:
- Vista administrador: Información completa del estudiante
- Auditoría y gestión académica
- Reporte de desempeño integral

---

#### 5. Actualizar Estudiante
```
PUT /api/v1/students/{id}
Content-Type: application/json
```
**Descripción**: Actualiza información del estudiante.

**Parámetros**:
- `id` (path, required): ID del estudiante

**Body** (campos opcionales):
```json
{
  "email": "juan.g.nuevo@student.edu",
  "classroomId": 2,
  "phone": "+56912345678"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "García",
  "email": "juan.g.nuevo@student.edu",
  "classroomId": 2,
  "phone": "+56912345678"
}
```

**Casos de Uso**:
- Actualizar información de contacto
- Cambiar aula del estudiante
- Modificar datos personales

---

#### 6. Eliminar Estudiante
```
DELETE /api/v1/students/{id}
```
**Descripción**: Elimina un estudiante del sistema.

**Parámetros**:
- `id` (path, required): ID del estudiante

**Respuesta Exitosa (204)**: Sin contenido

**Casos de Uso**:
- Dar de baja un estudiante
- Remover registros de prueba
- Limpiar errores de importación

---

#### 7. Verificar Existencia de Estudiante
```
GET /api/v1/students/{id}/exists
```
**Descripción**: Verifica si un estudiante existe en el sistema.

**Parámetros**:
- `id` (path, required): ID del estudiante

**Respuesta Exitosa (200)**:
```
true
```
o
```
false
```

**Casos de Uso**:
- Validar estudiante antes de asignar calificación
- Verificar en importación de datos
- Prevenir asignación de evaluaciones a estudiantes inexistentes

---

## Microservicio: BFF Web

**Base URL**: `http://localhost:3000` (En desarrollo)

### Dashboard Controller - Agregación de Datos

**Estado**: En desarrollo - Sin endpoints implementados

**Propósito**: El BFF (Backend For Frontend) será el punto centralizado que agregará datos de los microservicios y los presentará en formatos optimizados para el frontend.

**Rutas Futuras Planeadas**:
- `/api/v1/dashboard/student/{id}` - Dashboard del estudiante
- `/api/v1/dashboard/teacher/{id}` - Dashboard del profesor
- `/api/v1/dashboard/admin` - Dashboard administrativo
- `/api/v1/dashboard/classroom/{id}` - Dashboard de aula

---

## Relaciones entre Endpoints

### Flujo de Datos Principal

```
                    ┌─────────────────┐
                    │   ESTUDIANTES   │
                    │ (Student Mgr)   │
                    └────────┬────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
                ▼            ▼            ▼
            Asignaturas  Aulas       Evaluaciones
            (Subjects)  (Classroom)  (Assessments)
                            │
                    ┌───────┴────────┐
                    │                │
               (Courses)         (Grades)
               relaciones         resultados
                    │                │
                    └────────┬────────┘
                             │
                             ▼
                      BFF (Agregador)
                             │
                             ▼
                         Frontend
```

### Relaciones Clave

#### 1. **Estudiante → Aula → Asignaturas**
```
GET /api/v1/students/{id}/profile
└── Contiene classroomId
    └── GET /api/v1/classroom/{classroomId}
        └── Obtiene cursos con GET /api/v1/courses/search?classroomId=X
            └── Cada curso tiene una asignatura (subjectId)
                └── GET /api/v1/subjects/{subjectId}
```

#### 2. **Estudiante → Calificaciones → Evaluaciones**
```
GET /api/v1/students/{id}/full
└── Contiene allGrades[]
    └── Cada grade tiene assessmentId
        └── GET /api/v1/assessments/{assessmentId}
            └── Obtiene información completa de la evaluación
```

#### 3. **Aula → Plan Académico**
```
GET /api/v1/classroom/{id}
└── GET /api/v1/courses/search?classroomId=X
    └── Lista todas las asignaturas de la aula
        └── Cada curso contiene subjectId y teacher
            └── GET /api/v1/subjects/{subjectId}
```

#### 4. **Asignatura → Evaluaciones → Calificaciones**
```
GET /api/v1/subjects/{id}
└── GET /api/v1/assessments/search?courseId=X
    └── Obtiene evaluaciones de una asignatura
        └── Para cada assessment, GET /api/v1/grades/search?assessment=X
            └── Obtiene todas las notas de esa evaluación
```

### Matriz de Dependencias

| Endpoint | Depende de | Se relaciona con |
|----------|-----------|------------------|
| `POST /students` | Nada | classroomId (debe existir) |
| `POST /classroom` | Nada | Independiente |
| `POST /subjects` | Nada | Independiente |
| `POST /courses` | classroomId, subjectId | Vincula aula y asignatura |
| `POST /assessments` | courseId | Vinculado a asignatura |
| `POST /grades` | studentId, assessmentId | Vincula estudiante con evaluación |

---

## Casos de Uso Futuros para BFF

### 1. Dashboard Estudiante
```
GET /api/v1/bff/dashboard/student/{id}
```

**Descripción**: Proporciona una vista consolidada del desempeño del estudiante.

**Datos Agregados**:
```json
{
  "studentInfo": {
    "id": 1,
    "name": "Juan García",
    "classroom": "4to A"
  },
  "currentCourses": [
    {
      "id": 1,
      "subject": "Matemáticas",
      "teacher": "Prof. Juan",
      "semester": "Primer Semestre"
    }
  ],
  "upcomingAssessments": [
    {
      "id": 10,
      "title": "Examen Parcial",
      "date": "2024-06-15",
      "subject": "Matemáticas"
    }
  ],
  "recentGrades": [
    {
      "assessment": "Tarea 1",
      "score": 85,
      "subject": "Matemáticas",
      "date": "2024-05-20"
    }
  ],
  "performanceMetrics": {
    "averageGPA": 3.8,
    "passRate": "95%",
    "trend": "IMPROVING"
  }
}
```

**Servidor Interno BFF**:
```
1. GET /api/v1/students/{id}/profile (Student Mgr)
2. GET /api/v1/courses/search?classroomId=X (Classroom Mgr)
3. GET /api/v1/assessments/search?courseId=X (Assessment Mgr)
4. GET /api/v1/grades/search?studentId=X (Assessment Mgr)
5. Agregar y formatear datos
```

---

### 2. Dashboard Profesor
```
GET /api/v1/bff/dashboard/teacher/{id}
```

**Descripción**: Vista consolidada para gestión de clases del profesor.

**Datos Agregados**:
```json
{
  "teacherInfo": {
    "id": 101,
    "name": "Prof. Juan García"
  },
  "assignedCourses": [
    {
      "id": 1,
      "subject": "Matemáticas",
      "classroom": "4to A",
      "students": 30,
      "semester": "Primer Semestre"
    }
  ],
  "classDetails": [
    {
      "courseId": 1,
      "subject": "Matemáticas",
      "classroom": "4to A",
      "students": [
        {
          "id": 1,
          "name": "Juan García",
          "lastGrade": 85
        }
      ],
      "upcomingAssessments": [
        {
          "id": 10,
          "title": "Examen Final",
          "date": "2024-06-20"
        }
      ]
    }
  ],
  "gradingStats": {
    "totalStudents": 30,
    "averageScore": 82.5,
    "pendingGrades": 15
  }
}
```

---

### 3. Dashboard Administrativo
```
GET /api/v1/bff/dashboard/admin
```

**Descripción**: Vista integral del sistema académico completo.

**Datos Agregados**:
```json
{
  "systemStats": {
    "totalStudents": 450,
    "totalTeachers": 45,
    "totalClassrooms": 15,
    "totalSubjects": 22
  },
  "classroomSummary": [
    {
      "id": 1,
      "name": "4to A",
      "year": 2024,
      "students": 30,
      "averageGPA": 3.7,
      "subjects": 6
    }
  ],
  "performanceReport": {
    "overallAverageGPA": 3.65,
    "passRate": "92%",
    "failingStudents": 36
  },
  "academicCalendar": {
    "semester1": {
      "start": "2024-02-01",
      "end": "2024-07-15",
      "upcoming_exams": 45
    }
  }
}
```

---

### 4. Reporte de Calificaciones por Aula
```
GET /api/v1/bff/classroom/{id}/grades-report
```

**Descripción**: Reporte detallado de calificaciones de una aula.

**Datos Agregados**:
```json
{
  "classroom": "4to A",
  "period": "Primer Semestre 2024",
  "subjects": [
    {
      "name": "Matemáticas",
      "teacher": "Prof. Juan",
      "assessments": [
        {
          "title": "Prueba 1",
          "date": "2024-05-15",
          "studentGrades": [
            {
              "studentName": "Juan García",
              "score": 85,
              "feedback": "Buen desempeño"
            }
          ]
        }
      ],
      "classAverage": 81.5
    }
  ]
}
```

---

### 5. Búsqueda y Filtrado Avanzado
```
GET /api/v1/bff/search?type=assessment&filters=...
```

**Descripción**: Búsqueda consolidada en múltiples microservicios.

**Ejemplos de Uso**:
```
GET /api/v1/bff/search?type=assessment&classroom=1&semester=1
GET /api/v1/bff/search?type=student&classroom=1&minGPA=3.5
GET /api/v1/bff/search?type=course&teacher=Juan&year=2024
```

---

### 6. Historial y Auditoría
```
GET /api/v1/bff/audit/student/{id}
GET /api/v1/bff/audit/grade/{id}
```

**Descripción**: Rastreo de cambios y auditoría completa.

**Datos**:
```json
{
  "entityId": 1,
  "entityType": "GRADE",
  "history": [
    {
      "timestamp": "2024-05-21T10:30:00",
      "action": "UPDATED",
      "changedBy": "teacher@school.edu",
      "changes": {
        "score": "80.0 → 85.5",
        "feedback": "null → Corrección realizada"
      }
    }
  ]
}
```

---

### 7. Exportación y Generación de Reportes
```
POST /api/v1/bff/export/grades
POST /api/v1/bff/export/classroom-report
POST /api/v1/bff/generate-pdf/report/{type}
```

**Descripción**: Generar reportes en distintos formatos.

**Cuerpos de Solicitud**:
```json
{
  "format": "PDF",
  "type": "GRADES_REPORT",
  "filters": {
    "classroom": 1,
    "semester": "Primer Semestre",
    "year": 2024
  }
}
```

---

### 8. Sincronización en Tiempo Real
```
WebSocket: /api/v1/bff/ws/notifications
```

**Descripción**: Notificaciones en tiempo real de eventos académicos.

**Eventos**:
```json
{
  "type": "GRADE_POSTED",
  "data": {
    "studentId": 1,
    "assessmentTitle": "Prueba Matemáticas",
    "score": 85
  }
}
```

```json
{
  "type": "ASSESSMENT_CREATED",
  "data": {
    "courseId": 1,
    "assessmentTitle": "Nuevo Examen",
    "dueDate": "2024-06-20"
  }
}
```

---

### 9. Integración con Calendario
```
GET /api/v1/bff/calendar/{year}/{month}
```

**Descripción**: Vista de calendario con eventos académicos.

**Datos**:
```json
{
  "month": "June",
  "year": 2024,
  "events": [
    {
      "date": "2024-06-15",
      "assessments": [
        {
          "id": 10,
          "title": "Examen Final Matemáticas",
          "classroom": "4to A",
          "time": "14:00"
        }
      ]
    }
  ]
}
```

---

### 10. Analytics y Predictivos
```
GET /api/v1/bff/analytics/student-performance
GET /api/v1/bff/analytics/at-risk-students
```

**Descripción**: Análisis y predicciones de desempeño.

**Datos**:
```json
{
  "atRiskStudents": [
    {
      "id": 1,
      "name": "Juan García",
      "riskLevel": "HIGH",
      "reason": "Últimas 3 evaluaciones con calificaciones < 60",
      "recommendation": "Intervención académica recomendada"
    }
  ],
  "performanceTrends": {
    "improving": 120,
    "stable": 250,
    "declining": 80
  }
}
```

---

## Resumen de Endpoints Disponibles

| Microservicio | Recurso | Método | Endpoint |
|---------------|---------|--------|----------|
| **Assessment Manager** | Assessment | GET | `/api/v1/assessments/{id}` |
| | Assessment | GET | `/api/v1/assessments/search` |
| | Assessment | POST | `/api/v1/assessments` |
| | Assessment | PUT | `/api/v1/assessments/{id}` |
| | Assessment | DELETE | `/api/v1/assessments/{id}` |
| | Grade | GET | `/api/v1/grades/{id}` |
| | Grade | GET | `/api/v1/grades/search` |
| | Grade | POST | `/api/v1/grades` |
| | Grade | PUT | `/api/v1/grades/{id}` |
| | Grade | DELETE | `/api/v1/grades/{id}` |
| **Classroom Manager** | Classroom | GET | `/api/v1/classroom/{id}` |
| | Classroom | GET | `/api/v1/classroom/search` |
| | Classroom | POST | `/api/v1/classroom` |
| | Classroom | PUT | `/api/v1/classroom/{id}` |
| | Classroom | DELETE | `/api/v1/classroom/{id}` |
| | Course | GET | `/api/v1/courses/{id}` |
| | Course | GET | `/api/v1/courses/search` |
| | Course | POST | `/api/v1/courses` |
| | Course | PUT | `/api/v1/courses/{id}` |
| | Course | DELETE | `/api/v1/courses/{id}` |
| | Course | GET | `/api/v1/courses/{id}/exists` |
| | Subject | GET | `/api/v1/subjects/{id}` |
| | Subject | GET | `/api/v1/subjects/search` |
| | Subject | POST | `/api/v1/subjects` |
| | Subject | PUT | `/api/v1/subjects/{id}` |
| | Subject | DELETE | `/api/v1/subjects/{id}` |
| **Student Manager** | Student | POST | `/api/v1/students` |
| | Student | GET | `/api/v1/students` |
| | Student | GET | `/api/v1/students/{id}/profile` |
| | Student | GET | `/api/v1/students/{id}/full` |
| | Student | PUT | `/api/v1/students/{id}` |
| | Student | DELETE | `/api/v1/students/{id}` |
| | Student | GET | `/api/v1/students/{id}/exists` |

---

## Mejores Prácticas de Uso

### 1. Validación de Recursos
Siempre valida la existencia de recursos antes de operaciones CRUD:
```
Crear Grade → GET /api/v1/students/{studentId}/exists
            → GET /api/v1/assessments/{assessmentId} (si existe)

Crear Course → GET /api/v1/courses/{courseId}/exists (para referencia)
```

### 2. Flujo de Creación de Evaluaciones
```
1. POST /api/v1/subjects (crear asignatura si no existe)
2. POST /api/v1/classroom (crear aula si no existe)
3. POST /api/v1/courses (vincular asignatura a aula)
4. POST /api/v1/assessments (crear evaluación)
5. POST /api/v1/grades (registrar calificaciones)
```

### 3. Flujo de Matriculación de Estudiante
```
1. POST /api/v1/students (crear estudiante)
2. GET /api/v1/classroom (obtener aula)
3. PUT /api/v1/students/{id} (actualizar con classroomId)
4. GET /api/v1/courses/search?classroomId=X (ver sus cursos)
```

### 4. Búsquedas Eficientes
Usar filtros para reducir datos retornados:
```
GET /api/v1/assessments/search?courseId=1
En lugar de:
GET /api/v1/assessments/search (traer todo y filtrar)

GET /api/v1/grades/search?studentId=1&minScore=80
En lugar de:
GET /api/v1/grades/search (traer todo)
```

---

## Control de Versiones de API

Todos los endpoints usan API v1: `/api/v1/`

**Planes Futuros**:
- `/api/v2/` - Versión mejorada con nuevas funcionalidades
- `/api/v3/` - Integración con BFF consolidada

**Política de Deprecación**: Las versiones antiguas se mantendrán por 2 versiones principales.

---

## Notas Importantes

- ✅ Todos los endpoints soportan JSON
- ✅ Códigos de respuesta HTTP: 200 (OK), 201 (Created), 204 (No Content), 400 (Bad Request), 404 (Not Found), 500 (Server Error)
- ✅ Los IDs son Long (números enteros)
- ✅ Las fechas usan formato ISO 8601
- ⚠️ El BFF aún está en desarrollo y dispondrá de nuevos endpoints
- ⚠️ Validar siempre la lógica de negocio al integrar microservicios

---

**Última actualización**: 15 de Mayo de 2026  
**Versión del Documento**: 1.0  
**Estado**: Completo y Documentado

