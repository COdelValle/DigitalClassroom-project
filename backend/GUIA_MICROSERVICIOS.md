# 📚 Guía Completa - Digital Classroom Microservicios

**Última actualización**: 16 de Mayo de 2026  
**Versión**: 2.0  
**Estado**: Funcionalidades Básicas Completas

---

## 📖 Índice

1. [Descripción General](#descripción-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Microservicio: Student Manager](#microservicio-student-manager)
4. [Microservicio: Classroom Manager](#microservicio-classroom-manager)
5. [Microservicio: Assessment Manager](#microservicio-assessment-manager)
6. [Microservicio: BFF Web](#microservicio-bff-web)
7. [Comunicación Entre Microservicios](#comunicación-entre-microservicios)
8. [Flujos de Trabajo Recomendados](#flujos-de-trabajo-recomendados)
9. [Tutorial: Conectar desde Frontend](#tutorial-conectar-desde-frontend)
10. [Casos de Uso Futuros para BFF](#casos-de-uso-futuros-para-bff)

---

## Descripción General

**Digital Classroom** es una plataforma de gestión académica basada en una **arquitectura de microservicios desacoplados**. Cada microservicio maneja un dominio específico del negocio académico:

- **Student Manager**: Gestión de estudiantes, datos personales y representantes legales
- **Classroom Manager**: Gestión de aulas, asignaturas y relaciones entre ellas
- **Assessment Manager**: Gestión de evaluaciones y calificaciones
- **BFF Web**: Backend para el Frontend (en desarrollo) - Agregación de datos

### Características Principales

✅ **Validación cruzada** entre microservicios con Circuit Breaker (Resilience4j)  
✅ **API REST** con OpenAPI/Swagger documentada  
✅ **Bases de datos independientes** (MariaDB)  
✅ **Transaccionalidad** por microservicio  
✅ **Testing configurado** en cada módulo  

---

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                      FRONTEND (React + Vite)                    │
└──────────────────────┬──────────────────────────────────────────┘
                       │
        ┌──────────────┴─────────────────────┐
        │                                    │
   ┌────▼─────┐                      ┌──────▼──────┐
   │  BFF Web │  (En Desarrollo)     │ Direct Calls│
   │ :8080    │                      │ (Futuro)    │
   └────┬─────┘                      └─────┬───────┘
        │                                   │
        └───────────┬──────────────────────┘
                    │
        ┌───────────┼──────────────────┐
        │           │                  │
    ┌───▼────────┐ ┌▼───────────────┐ ┌▼──────────┐
    │  Assessment│ │ Classroom      │ │ Student   │
    │  Manager   │ │ Manager        │ │ Manager   │
    │  :8083     │ │ :8084          │ │ :8081     │
    └───┬────────┘ └┬───────────────┘ └┬──────────┘
        │          │                   │
        └──────────┴───────┬───────────┘
                           │
                ┌──────────▼──────────┐
                │   MariaDB Cluster   │
                │ (Bases de Datos     │
                │  Independientes)    │
                └─────────────────────┘
```

### Base de Datos

| Microservicio | Base de Datos | Puerto | Host |
|---------------|---------------|--------|------|
| Student Manager | `student_manager_db` | 3306 | localhost |
| Classroom Manager | `classroom_manager_db` | 3306 | localhost |
| Assessment Manager | `assessment_manager_db` | 3306 | localhost |
| BFF Web | (No aplica) | - | - |

---

## Microservicio: Student Manager

### ℹ️ Información General

**URL Base**: `http://localhost:8081`  
**Base de Datos**: `student_manager_db`  
**Ruta API**: `/api/v1/students`

**Responsabilidades**:
- ✅ Crear, leer, actualizar y eliminar estudiantes
- ✅ Gestionar representantes legales
- ✅ Registrar alergias y datos personales
- ✅ Validar datos únicos (RUT chileno)

---

### Entidad: Student

**Campos almacenados**:

```json
{
  "id": "Long - ID único (autogenerado)",
  "rut": "String - RUT Chileno único validado",
  "firstName": "String - Nombre(s)",
  "middleName": "String (opcional) - Segundo nombre",
  "lastName": "String - Apellido(s)",
  "birthDate": "Date - Fecha de nacimiento (pasada)",
  "allergies": "List<String> - Lista de alergias",
  "legalRepresentatives": "List<LegalRepresentativeDTO> - Representantes legales"
}
```

**LegalRepresentativeDTO**:

```json
{
  "rut": "String - RUT del representante (validado)",
  "fullName": "String - Nombre completo",
  "email": "String - Correo electrónico válido",
  "phoneNumber": "List<String> - Lista de números telefónicos",
  "relationship": "String - Parentesco (Padre, Madre, Tutor, etc.)"
}
```

---

### Endpoints: Student Controller

#### 1. Crear Estudiante

```http
POST /api/v1/students
Content-Type: application/json
```

**Descripción**: Registra un nuevo estudiante en el sistema.

**Request Body** (REQUERIDO - todos los campos):

```json
{
  "rut": "15.123.456-7",
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "García López",
  "birthDate": "2008-03-15",
  "allergies": ["Maní", "Lácteos"],
  "legalRepresentatives": [
    {
      "rut": "12.345.678-9",
      "fullName": "María García López",
      "email": "maria.garcia@email.com",
      "phoneNumber": ["+56 9 1111 2222", "+56 9 3333 4444"],
      "relationship": "Madre"
    }
  ]
}
```

**Response** (201 Created):

```json
{
  "id": 1,
  "rut": "15.123.456-7",
  "fullName": "Juan Carlos García López",
  "allergies": ["Maní", "Lácteos"],
  "emergencyContacts": [
    {
      "name": "María García López",
      "phoneNumbers": ["+56 9 1111 2222", "+56 9 3333 4444"],
      "relationship": "Madre"
    }
  ]
}
```

**Headers**:
- `Location: /api/v1/students/1`

**Validaciones**:
- ❌ RUT inválido
- ❌ RUT duplicado (ya existe)
- ❌ Nombre vacío o < 2 caracteres
- ❌ Fecha de nacimiento futura
- ❌ Sin representantes legales
- ❌ Representante sin RUT válido

**Casos de Uso**:
- Matricular nuevo estudiante
- Importación masiva de estudiantes
- Registro inicial de alumno

---

#### 2. Listar Todos los Estudiantes (Tabla)

```http
GET /api/v1/students
```

**Descripción**: Obtiene listado simplificado para tablas de visualización.

**Response** (200 OK):

```json
[
  {
    "id": 1,
    "rut": "15.123.456-7",
    "fullName": "Juan Carlos García López"
  },
  {
    "id": 2,
    "rut": "12.987.654-3",
    "fullName": "María González Silva"
  }
]
```

**Casos de Uso**:
- Cargar dropdowns
- Mostrar tablas de estudiantes
- Exportar listas de matrícula

---

#### 3. Obtener Perfil del Estudiante (Vista Profesor)

```http
GET /api/v1/students/{id}/profile
```

**Descripción**: Vista segura para profesores (información básica + contactos de emergencia).

**Path Parameters**:
- `id` (required, Long): ID del estudiante

**Response** (200 OK):

```json
{
  "id": 1,
  "rut": "15.123.456-7",
  "fullName": "Juan Carlos García López",
  "allergies": ["Maní", "Lácteos"],
  "emergencyContacts": [
    {
      "name": "María García López",
      "phoneNumbers": ["+56 9 1111 2222", "+56 9 3333 4444"],
      "relationship": "Madre"
    }
  ]
}
```

**Casos de Uso**:
- Vista de profesor antes de clase
- Checklist de alergias
- Información de emergencia en el aula

---

#### 4. Obtener Perfil Completo del Estudiante (Vista Admin)

```http
GET /api/v1/students/{id}/full
```

**Descripción**: Vista completa con todos los datos (solo para administradores).

**Path Parameters**:
- `id` (required, Long): ID del estudiante

**Response** (200 OK):

```json
{
  "id": 1,
  "rut": "15.123.456-7",
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "García López",
  "birthDate": "2008-03-15",
  "allergies": ["Maní", "Lácteos"],
  "legalRepresentatives": [
    {
      "rut": "12.345.678-9",
      "fullName": "María García López",
      "email": "maria.garcia@email.com",
      "phoneNumber": ["+56 9 1111 2222"],
      "relationship": "Madre"
    }
  ]
}
```

**Casos de Uso**:
- Vista administrativa completa
- Edición de datos personales
- Auditoría de información

---

#### 5. Actualizar Estudiante

```http
PUT /api/v1/students/{id}
Content-Type: application/json
```

**Descripción**: Actualiza información del estudiante. Todos los campos deben ser re-enviados.

**Path Parameters**:
- `id` (required, Long): ID del estudiante

**Request Body** (Completo - con cambios):

```json
{
  "rut": "15.123.456-7",
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "García López",
  "birthDate": "2008-03-15",
  "allergies": ["Maní"],
  "legalRepresentatives": [
    {
      "rut": "12.345.678-9",
      "fullName": "María García López",
      "email": "maria.garcia@email.com",
      "phoneNumber": ["+56 9 1111 2222"],
      "relationship": "Madre"
    }
  ]
}
```

**Response** (200 OK - Retorna Full Detail):

```json
{
  "id": 1,
  "rut": "15.123.456-7",
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "García López",
  "birthDate": "2008-03-15",
  "allergies": ["Maní"],
  "legalRepresentatives": [...]
}
```

**Casos de Uso**:
- Actualizar teléfono de representante
- Cambiar alergias registradas
- Corregir datos personales

---

#### 6. Eliminar Estudiante

```http
DELETE /api/v1/students/{id}
```

**Descripción**: Elimina un estudiante del sistema (borrado lógico o físico según configuración).

**Path Parameters**:
- `id` (required, Long): ID del estudiante

**Response** (204 No Content)

**⚠️ Cuidado**:
- Verificar que no tenga:
  - Calificaciones activas
  - Matrículas activas
  - Evaluaciones pendientes

**Casos de Uso**:
- Dar de baja estudiante
- Limpiar registros de prueba

---

#### 7. Verificar Existencia de Estudiante

```http
GET /api/v1/students/{id}/exists
```

**Descripción**: Verifica si un estudiante existe (usado por otros microservicios).

**Path Parameters**:
- `id` (required, Long): ID del estudiante

**Response** (200 OK):

```
true  o  false
```

**Nota**: Este endpoint es **CRÍTICO** para validaciones desde:
- Classroom Manager (crear aulas)
- Assessment Manager (asignar calificaciones)

---

---

## Microservicio: Classroom Manager

### ℹ️ Información General

**URL Base**: `http://localhost:8084`  
**Base de Datos**: `classroom_manager_db`  
**Rutas API**:
- `/api/v1/classroom` - Gestión de aulas
- `/api/v1/courses` - Relación aula-asignatura-profesor
- `/api/v1/subjects` - Gestión de asignaturas

**Responsabilidades**:
- ✅ Crear y gestionar aulas
- ✅ Crear y gestionar asignaturas
- ✅ Vincular asignaturas a aulas con profesores
- ✅ Validar existencia de estudiantes

---

### Entidad: Classroom

**Campos almacenados**:

```json
{
  "id": "Long - ID único",
  "code": "String - Código único (ej: 4A-2024)",
  "name": "String - Nombre descriptivo (ej: 4to A)",
  "schoolYear": "Integer - Año escolar",
  "studentIds": "List<Long> - IDs de estudiantes matriculados"
}
```

**Restricciones**:
- `code` es ÚNICO y REQUERIDO
- `schoolYear >= 2024`
- No vacío, mínimo 3 caracteres
- Las fechas no se almacenan

---

### Entidad: Subject

**Campos almacenados**:

```json
{
  "id": "Long - ID único",
  "name": "String - Nombre único de asignatura",
  "area": "String - Área de conocimiento",
  "isActive": "Boolean - Indica si está activa (default: true)"
}
```

---

### Entidad: Course

**Campos almacenados** (Relación Aula-Asignatura):

```json
{
  "id": "Long - ID único",
  "subject": "Subject - Referencia a la asignatura",
  "classroom": "Classroom - Referencia al aula",
  "schoolYear": "Integer - Año escolar",
  "semester": "String - Semestre (ej: Primer Semestre)",
  "teacherName": "String - Nombre del profesor"
}
```

---

### Endpoints: Subject Controller

#### 1. Obtener Asignatura por ID

```http
GET /api/v1/subjects/{id}
```

**Path Parameters**:
- `id` (required, Long): ID de la asignatura

**Response** (200 OK):

```json
{
  "id": 1,
  "name": "Matemáticas",
  "area": "Ciencias Exactas",
  "isActive": true
}
```

---

#### 2. Buscar Asignaturas

```http
GET /api/v1/subjects/search?name=Matemáticas&area=Ciencias
```

**Query Parameters** (opcionales):
- `name`: Filtra por nombre
- `area`: Filtra por área

**Response** (200 OK):

```json
[
  {
    "id": 1,
    "name": "Matemáticas",
    "area": "Ciencias Exactas",
    "isActive": true
  }
]
```

---

#### 3. Crear Asignatura

```http
POST /api/v1/subjects
Content-Type: application/json
```

**Request Body**:

```json
{
  "name": "Química",
  "area": "Ciencias Exactas"
}
```

**Response** (201 Created):

```json
{
  "id": 2,
  "name": "Química",
  "area": "Ciencias Exactas",
  "isActive": true
}
```

**Validaciones**:
- ❌ Nombre vacío
- ❌ Nombre duplicado
- ❌ Área vacía

---

#### 4. Actualizar Asignatura

```http
PUT /api/v1/subjects/{id}
Content-Type: application/json
```

**Path Parameters**:
- `id` (required, Long): ID de la asignatura

**Request Body**:

```json
{
  "name": "Química Avanzada",
  "area": "Ciencias Exactas"
}
```

**Response** (200 OK):

```json
{
  "id": 2,
  "name": "Química Avanzada",
  "area": "Ciencias Exactas",
  "isActive": true
}
```

---

#### 5. Eliminar Asignatura

```http
DELETE /api/v1/subjects/{id}
```

**Path Parameters**:
- `id` (required, Long): ID de la asignatura

**Response** (204 No Content)

✅ Los cursos vinculados determinan si se puede eliminar

---

### Endpoints: Classroom Controller

#### 1. Obtener Aula por ID

```http
GET /api/v1/classroom/{id}
```

**Path Parameters**:
- `id` (required, Long): ID del aula

**Response** (200 OK):

```json
{
  "id": 1,
  "code": "4A-2024",
  "name": "4to A",
  "schoolYear": 2024,
  "studentIds": [1, 2, 3],
  "courses": [
    {
      "id": 1,
      "subjectName": "Matemáticas",
      "teacherName": "Prof. Juan Pérez",
      "semester": "Primer Semestre"
    }
  ]
}
```

---

#### 2. Buscar Aulas

```http
GET /api/v1/classroom/search?name=4to&year=2024
```

**Query Parameters** (opcionales):
- `name`: Filtra por nombre
- `year`: Filtra por año (schoolYear)

**Response** (200 OK):

```json
[
  {
    "id": 1,
    "code": "4A-2024",
    "name": "4to A",
    "schoolYear": 2024,
    "studentIds": [1, 2, 3],
    "courses": []
  }
]
```

---

#### 3. Crear Aula

```http
POST /api/v1/classroom
Content-Type: application/json
```

**Request Body**:

```json
{
  "code": "4A-2024",
  "name": "4to A",
  "schoolYear": 2024,
  "studentIds": [1, 2, 3, 4]
}
```

**Response** (201 Created):

```json
{
  "id": 1,
  "code": "4A-2024",
  "name": "4to A",
  "schoolYear": 2024,
  "studentIds": [1, 2, 3, 4],
  "courses": []
}
```

**Validaciones** (consulta Student Manager):
- ❌ Código vacío o duplicado
- ❌ Nombre corto (< 3 caracteres)
- ❌ Año < 2024
- ❌ Estudiante inexistente (valida con Student Manager)

---

#### 4. Actualizar Aula

```http
PUT /api/v1/classroom/{id}
Content-Type: application/json
```

**Path Parameters**:
- `id` (required, Long): ID del aula

**Request Body** (solo campos a actualizar):

```json
{
  "name": "4to A - Jornada Vespertina",
  "studentIds": [1, 2, 3, 5]
}
```

**Response** (200 OK)

---

#### 5. Eliminar Aula

```http
DELETE /api/v1/classroom/{id}
```

**⚠️ Restricción**: No se puede eliminar si tiene cursos activos

**Response** (204 No Content)

---

### Endpoints: Course Controller

#### 1. Obtener Relación Curso-Asignatura

```http
GET /api/v1/courses/{id}
```

**Response** (200 OK):

```json
{
  "id": 1,
  "subject": {
    "id": 1,
    "name": "Matemáticas",
    "area": "Ciencias Exactas",
    "isActive": true
  },
  "classroomId": 1,
  "classroomName": "4to A",
  "schoolYear": 2024,
  "semester": "Primer Semestre",
  "teacherName": "Prof. Juan Pérez"
}
```

---

#### 2. Buscar Cursos

```http
GET /api/v1/courses/search?classroomId=1&teacher=Juan&semester=Primer
```

**Query Parameters** (opcionales):
- `classroomId`: ID del aula
- `teacher`: Nombre del profesor
- `semester`: Semestre

**Response** (200 OK):

```json
[
  {
    "id": 1,
    "subject": {...},
    "classroomId": 1,
    "classroomName": "4to A",
    "schoolYear": 2024,
    "semester": "Primer Semestre",
    "teacherName": "Prof. Juan Pérez"
  }
]
```

---

#### 3. Crear Curso (Vincular Asignatura a Aula)

```http
POST /api/v1/courses
Content-Type: application/json
```

**Request Body**:

```json
{
  "subjectId": 1,
  "classroomId": 1,
  "schoolYear": 2024,
  "semester": "Primer Semestre",
  "teacherName": "Prof. Juan Pérez"
}
```

**Response** (201 Created):

```json
{
  "id": 1,
  "subject": {
    "id": 1,
    "name": "Matemáticas",
    "area": "Ciencias Exactas",
    "isActive": true
  },
  "classroomId": 1,
  "classroomName": "4to A",
  "schoolYear": 2024,
  "semester": "Primer Semestre",
  "teacherName": "Prof. Juan Pérez"
}
```

**Validaciones**:
- ❌ Asignatura inexistente
- ❌ Aula inexistente
- ❌ Combinación duplicada (misma asignatura, aula, año, semestre)

---

#### 4. Actualizar Curso

```http
PUT /api/v1/courses/{id}
Content-Type: application/json
```

**Request Body**:

```json
{
  "teacherName": "Prof. Carlos Martínez",
  "semester": "Segundo Semestre"
}
```

**Response** (200 OK)

---

#### 5. Eliminar Curso

```http
DELETE /api/v1/courses/{id}
```

**Response** (204 No Content)

---

#### 6. Verificar Existencia de Curso

```http
GET /api/v1/courses/{id}/exists
```

**Response** (200 OK):

```
true  o  false
```

**Nota**: Usado por Assessment Manager para validar evaluaciones

---

---

## Microservicio: Assessment Manager

### ℹ️ Información General

**URL Base**: `http://localhost:8083`  
**Base de Datos**: `assessment_manager_db`  
**Rutas API**:
- `/api/v1/assessments` - Gestión de evaluaciones
- `/api/v1/grades` - Gestión de calificaciones

**Responsabilidades**:
- ✅ Crear y gestionar evaluaciones
- ✅ Registrar y modificar calificaciones
- ✅ Validar estudiantes (con Classroom Manager)
- ✅ Validar cursos (con Assessment Manager)

---

### Entidad: Assessment

**Campos almacenados**:

```json
{
  "id": "Long - ID único",
  "title": "String - Título de la evaluación",
  "courseId": "Long - ID de la relación Curso",
  "examDate": "LocalDate - Fecha de examen (formato YYYY-MM-DD)",
  "grades": "List<Grade> - Calificaciones asociadas",
  "isGraded": "boolean - Indica si tiene calificaciones"
}
```

---

### Entidad: Grade

**Campos almacenados**:

```json
{
  "id": "Long - ID único",
  "studentId": "Long - ID del estudiante",
  "score": "Double - Puntaje (validación chilena 1.0-7.0)",
  "registrationDate": "LocalDateTime - Fecha/hora de creación automática"
}
```

**Validación de Puntaje (ChileanGrade)**:
- ✅ Rango: 1.0 - 7.0
- ✅ Acepta decimales (ej: 5.5)
- ❌ Rechaza fuera de rango

---

### Endpoints: Assessment Controller

#### 1. Obtener Evaluación por ID

```http
GET /api/v1/assessments/{id}
```

**Response** (200 OK):

```json
{
  "id": 1,
  "title": "Examen de Matemáticas",
  "courseId": 1,
  "examDate": "2024-06-15",
  "isGraded": true,
  "grades": [
    {
      "id": 10,
      "studentId": 1,
      "score": 6.5,
      "registrationDate": "2024-06-16T10:30:00"
    }
  ]
}
```

---

#### 2. Buscar Evaluaciones

```http
GET /api/v1/assessments/search?courseId=1&title=Examen
```

**Query Parameters** (opcionales):
- `courseId`: ID del curso
- `title`: Título de la evaluación
- `examDate`: Fecha (formato: YYYY-MM-DD)

**Response** (200 OK):

```json
[
  {
    "id": 1,
    "title": "Examen de Matemáticas",
    "courseId": 1,
    "examDate": "2024-06-15",
    "isGraded": true,
    "grades": []
  }
]
```

---

#### 3. Crear Evaluación

```http
POST /api/v1/assessments
Content-Type: application/json
```

**Request Body**:

```json
{
  "title": "Examen Final",
  "courseId": 1,
  "examDate": "2024-06-20",
  "grades": [
    {
      "studentId": 1,
      "score": 6.5
    },
    {
      "studentId": 2,
      "score": 5.0
    }
  ]
}
```

**Response** (201 Created):

```json
{
  "id": 1,
  "title": "Examen Final",
  "courseId": 1,
  "examDate": "2024-06-20",
  "isGraded": true,
  "grades": [
    {
      "id": 10,
      "studentId": 1,
      "score": 6.5,
      "registrationDate": "2024-06-20T14:00:00"
    }
  ]
}
```

**Validaciones** (consulta otros microservicios):
- ❌ Curso inexistente (consulta Classroom Manager)
- ❌ Estudiante inexistente (consulta Student Manager)
- ❌ Puntaje fuera de rango (1.0-7.0)
- ❌ examDate vacío
- ❌ Título vacío

**Nota**: `grades` es OPCIONAL. Puedes crear sin calificaciones.

---

#### 4. Actualizar Evaluación

```http
PUT /api/v1/assessments/{id}
Content-Type: application/json
```

**Request Body** (campos opcionales):

```json
{
  "title": "Examen Final Modificado",
  "examDate": "2024-06-22"
}
```

**Response** (200 OK)

---

#### 5. Eliminar Evaluación

```http
DELETE /api/v1/assessments/{id}
```

**⚠️ Restricciones de Borrado**:
- ❌ NO se puede eliminar si es del año actual Y tiene calificaciones
- ❌ NO se puede eliminar si es de años anteriores (datos históricos)

**Response** (204 No Content) o (400 Bad Request)

---

### Endpoints: Grade Controller

#### 1. Obtener Calificación por ID

```http
GET /api/v1/grades/{id}
```

**Response** (200 OK):

```json
{
  "id": 10,
  "studentId": 1,
  "score": 6.5,
  "registrationDate": "2024-06-20T14:00:00"
}
```

---

#### 2. Buscar Calificaciones

```http
GET /api/v1/grades/search?studentId=1&minScore=5.0&maxScore=7.0
```

**Query Parameters** (opcionales):
- `studentId`: ID del estudiante
- `minScore`: Puntaje mínimo
- `maxScore`: Puntaje máximo
- `date`: Fecha de registro

**Response** (200 OK):

```json
[
  {
    "id": 10,
    "studentId": 1,
    "score": 6.5,
    "registrationDate": "2024-06-20T14:00:00"
  },
  {
    "id": 11,
    "studentId": 1,
    "score": 5.5,
    "registrationDate": "2024-06-21T15:30:00"
  }
]
```

---

#### 3. Crear Calificación (Independiente)

```http
POST /api/v1/grades
Content-Type: application/json
```

**Request Body**:

```json
{
  "studentId": 1,
  "assessmentId": 1,
  "score": 6.5
}
```

**Response** (201 Created):

```json
{
  "id": 10,
  "studentId": 1,
  "score": 6.5,
  "registrationDate": "2024-06-20T14:00:00"
}
```

**Validaciones**:
- ❌ Estudiante inexistente
- ❌ Evaluación inexistente
- ❌ Puntaje fuera de rango

**Nota**: La `registrationDate` se genera automáticamente

---

#### 4. Actualizar Calificación

```http
PUT /api/v1/grades/{id}
Content-Type: application/json
```

**Request Body**:

```json
{
  "score": 7.0,
  "studentId": 2
}
```

**Response** (200 OK)

**Nota**: Puedes cambiar el estudiante, pero se valida existencia

---

#### 5. Eliminar Calificación

```http
DELETE /api/v1/grades/{id}
```

**⚠️ Restricción**:
- ❌ NO se puede eliminar si es de años académicos anteriores

**Response** (204 No Content)

---

---

## Microservicio: BFF Web

### ℹ️ Información General

**URL Base**: `http://localhost:8080` (próximamente)  
**Estado**: ⚠️ **EN DESARROLLO**

**Propósito**:
- Agregar datos de múltiples microservicios
- Convertir datos para el Frontend
- Cachear datos frecuentes
- Implementar lógica de negocio compleja

**Tecnología**:
- OpenFeign (cliente HTTP con Circuit Breaker)
- Resilience4j (manejo de fallos)
- Spring Security (OAuth2)

---

### Estructura Actual

```
src/main/java/cl/digitalclassroom/bffweb/
├── BffWebApplication.java
├── controller/
│   └── DashboardController.java (vacío)
├── client/
│   ├── StudentClient.java (vacío)
│   ├── ClassroomClient.java (vacío)
│   └── AssessmentClient.java (vacío)
├── config/
│   ├── FeignConfig.java (vacío)
│   └── SecurityConfig.java (vacío)
├── service/
│   ├── DashboardService.java (vacío)
│   └── impl/
└── dto/
    ├── request/ (vacío)
    └── response/ (vacío)
```

---

### Endpoints Planificados (Próxima Fase)

#### 1. Dashboard de Estudiante

```http
GET /api/v1/bff/dashboard/student/{id}
```

**Agregación de datos**:
1. GET `/api/v1/students/{id}/profile` (Student Manager)
2. GET `/api/v1/courses/search?classroomId=X` (Classroom Manager)
3. GET `/api/v1/assessments/search?courseId=X` (Assessment Manager)
4. GET `/api/v1/grades/search?studentId=X` (Assessment Manager)

**Response esperado**:

```json
{
  "studentInfo": {
    "id": 1,
    "fullName": "Juan García",
    "rut": "15.123.456-7",
    "allergies": ["Maní"]
  },
  "currentCourses": [
    {
      "id": 1,
      "subject": "Matemáticas",
      "teacher": "Prof. Juan Pérez",
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
      "score": 6.5,
      "subject": "Matemáticas",
      "date": "2024-06-20"
    }
  ]
}
```

---

---

## Comunicación Entre Microservicios

### Patrón de Sincronización

Usamos **OpenFeign + Resilience4j Circuit Breaker** para comunicación síncrona entre servicios.

```
┌──────────────────────┐
│ Assessment Manager   │
└──────┬───────────────┘
       │
       ├─► StudentFeignClient
       │   └─► GET /api/v1/students/{id}/exists
       │       (Student Manager :8081)
       │
       └─► AcademicFeignClient
           └─► GET /api/v1/courses/{courseId}/exists
               (Classroom Manager :8084)
```

### Ejemplo: Crear Evaluación

```
1. POST /api/v1/assessments
   {
     "title": "Examen",
     "courseId": 1,
     "examDate": "2024-06-20",
     "grades": [{"studentId": 1, "score": 6.5}]
   }

2. Assessment Manager valida:
   ✓ courseId existe? → Classroom Manager (8084)
   ✓ studentId existe? → Student Manager (8081)

3. Si todas las validaciones pasan:
   ✓ Guardar en DB assessment_manager_db

4. Retornar 201 Created + Location header
```

### Circuit Breaker (Resilience4j)

**Si Student Manager falla** (timeout, 500, etc.):

```
Intento 1: Llamada normal
  ↓ FALLA
Intento 2-3: Reintentos automáticos
  ↓ FALLAN
Circuit Breaker SE ABRE
  ↓
Respuesta: ServiceUnavailableException
  "El servicio de validación estudiantil no responde..."
```

**Configuración**:

```properties
resilience4j.circuitbreaker.instances.studentServiceCB.slidingWindowSize=10
resilience4j.circuitbreaker.instances.studentServiceCB.failureRateThreshold=50
resilience4j.circuitbreaker.instances.studentServiceCB.waitDurationInOpenState=20s
```

---

---

## Flujos de Trabajo Recomendados

### 1️⃣ Flujo: Crear Clase Completa

```
PASO 1: Crear Asignatura
  POST /api/v1/subjects
  {
    "name": "Matemáticas",
    "area": "Ciencias Exactas"
  }
  → Retorna: { "id": 1, "name": "Matemáticas", ... }

PASO 2: Crear Aula
  POST /api/v1/classroom
  {
    "code": "4A-2024",
    "name": "4to A",
    "schoolYear": 2024,
    "studentIds": [1, 2, 3]  ← Validar existencia first
  }
  → Retorna: { "id": 1, "code": "4A-2024", ... }

PASO 3: Vincular Asignatura a Aula
  POST /api/v1/courses
  {
    "subjectId": 1,
    "classroomId": 1,
    "schoolYear": 2024,
    "semester": "Primer Semestre",
    "teacherName": "Prof. Juan Pérez"
  }
  → Retorna: { "id": 1, "subject": {...}, ... }

PASO 4: Crear Evaluación
  POST /api/v1/assessments
  {
    "title": "Examen",
    "courseId": 1,          ← Del PASO 3
    "examDate": "2024-06-15",
    "grades": []
  }
  → Retorna: { "id": 1, "title": "Examen", ... }

PASO 5: Registrar Calificaciones
  POST /api/v1/grades
  {
    "studentId": 1,
    "assessmentId": 1,      ← Del PASO 4
    "score": 6.5
  }
  → Retorna: { "id": 1, "studentId": 1, "score": 6.5 }
```

### 2️⃣ Flujo: Matricular Estudiante

```
PASO 1: Crear Estudiante
  POST /api/v1/students
  {
    "rut": "15.123.456-7",
    "firstName": "Juan",
    "lastName": "García",
    "birthDate": "2008-03-15",
    "allergies": ["Maní"],
    "legalRepresentatives": [...]
  }
  → Retorna: { "id": 1, ... }

PASO 2: Agregar a Aula
  PUT /api/v1/classroom/{id}
  {
    "name": "4to A",
    "studentIds": [1, 2, 3, 4]  ← Incluir el nuevo
  }

PASO 3: Obtener Cursos del Aula
  GET /api/v1/courses/search?classroomId=1
  → Retorna cursos donde está matriculado

PASO 4: Ver Evaluaciones de sus Cursos
  GET /api/v1/assessments/search?courseId=1
  → Retorna evaluaciones

PASO 5: Ver sus Calificaciones
  GET /api/v1/grades/search?studentId=1
  → Retorna todas las notas
```

---

---

## Tutorial: Conectar desde Frontend

### 1. Configuración Base (React + Vite)

**Crear archivo:** `src/api/config.ts`

```javascript
// Configuración base según ambiente
const API_BASE_URL = {
  development: 'http://localhost',
  production: 'https://api.digitalclassroom.cl'
};

const SERVICES = {
  STUDENT: process.env.REACT_APP_STUDENT_URL || 'http://localhost:8081',
  CLASSROOM: process.env.REACT_APP_CLASSROOM_URL || 'http://localhost:8084',
  ASSESSMENT: process.env.REACT_APP_ASSESSMENT_URL || 'http://localhost:8083'
};

export const API_CONFIG = {
  baseURL: API_BASE_URL[process.env.NODE_ENV] || API_BASE_URL.development,
  services: SERVICES,
  timeout: 30000
};
```

---

### 2. Cliente HTTP (Axios)

**Crear archivo:** `src/api/client.ts`

```javascript
import axios from 'axios';
import { API_CONFIG } from './config';

// Cliente para cada servicio
export const studentClient = axios.create({
  baseURL: `${API_CONFIG.services.STUDENT}/api/v1`,
  timeout: API_CONFIG.timeout
});

export const classroomClient = axios.create({
  baseURL: `${API_CONFIG.services.CLASSROOM}/api/v1`,
  timeout: API_CONFIG.timeout
});

export const assessmentClient = axios.create({
  baseURL: `${API_CONFIG.services.ASSESSMENT}/api/v1`,
  timeout: API_CONFIG.timeout
});

// Interceptor para errores
const handleError = (error) => {
  const message = error.response?.data?.message || error.message;
  console.error('API Error:', message);
  throw new Error(message);
};

[studentClient, classroomClient, assessmentClient].forEach(client => {
  client.interceptors.response.use(
    response => response,
    handleError
  );
});

export default { studentClient, classroomClient, assessmentClient };
```

---

### 3. Servicios API (por Recurso)

**Crear archivo:** `src/api/studentService.ts`

```javascript
import { studentClient } from './client';

export const studentService = {
  // Crear estudiante
  createStudent: (data) => 
    studentClient.post('/students', data),

  // Obtener todos (lista simple)
  getAllStudents: () =>
    studentClient.get('/students'),

  // Obtener perfil (profesor)
  getStudentProfile: (id) =>
    studentClient.get(`/students/${id}/profile`),

  // Obtener detalle completo (admin)
  getStudentFull: (id) =>
    studentClient.get(`/students/${id}/full`),

  // Actualizar
  updateStudent: (id, data) =>
    studentClient.put(`/students/${id}`, data),

  // Eliminar
  deleteStudent: (id) =>
    studentClient.delete(`/students/${id}`),

  // Verificar existencia
  checkExists: (id) =>
    studentClient.get(`/students/${id}/exists`)
};
```

**Crear archivo:** `src/api/classroomService.ts`

```javascript
import { classroomClient } from './client';

export const classroomService = {
  // CLASSROOM
  getClassroom: (id) =>
    classroomClient.get(`/classroom/${id}`),

  searchClassrooms: (filters) =>
    classroomClient.get('/classroom/search', { params: filters }),

  createClassroom: (data) =>
    classroomClient.post('/classroom', data),

  updateClassroom: (id, data) =>
    classroomClient.put(`/classroom/${id}`, data),

  deleteClassroom: (id) =>
    classroomClient.delete(`/classroom/${id}`),

  // COURSE
  getCourse: (id) =>
    classroomClient.get(`/courses/${id}`),

  searchCourses: (filters) =>
    classroomClient.get('/courses/search', { params: filters }),

  createCourse: (data) =>
    classroomClient.post('/courses', data),

  updateCourse: (id, data) =>
    classroomClient.put(`/courses/${id}`, data),

  deleteCourse: (id) =>
    classroomClient.delete(`/courses/${id}`),

  verifyCourse: (id) =>
    classroomClient.get(`/courses/${id}/exists`),

  // SUBJECT
  getSubject: (id) =>
    classroomClient.get(`/subjects/${id}`),

  searchSubjects: (filters) =>
    classroomClient.get('/subjects/search', { params: filters }),

  createSubject: (data) =>
    classroomClient.post('/subjects', data),

  updateSubject: (id, data) =>
    classroomClient.put(`/subjects/${id}`, data),

  deleteSubject: (id) =>
    classroomClient.delete(`/subjects/${id}`)
};
```

---

### 4. Hook personalizado

**Crear archivo:** `src/hooks/useStudents.ts`

```javascript
import { useState, useEffect } from 'react';
import { studentService } from '../api/studentService';

export function useStudents() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchStudents = async () => {
    try {
      setLoading(true);
      const response = await studentService.getAllStudents();
      setStudents(response.data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStudents();
  }, []);

  return { students, loading, error, refetch: fetchStudents };
}
```

---

### 5. Ejemplo en Componente React

**Crear archivo:** `src/pages/Students.jsx`

```jsx
import { useState } from 'react';
import { useStudents } from '../hooks/useStudents';
import { studentService } from '../api/studentService';

export function StudentsList() {
  const { students, loading, error } = useStudents();
  const [creating, setCreating] = useState(false);

  const handleCreateStudent = async (formData) => {
    try {
      setCreating(true);
      const response = await studentService.createStudent(formData);
      console.log('Estudiante creado:', response.data);
      // Refrescar lista, mostrar toast, etc.
    } catch (err) {
      console.error('Error:', err.message);
    } finally {
      setCreating(false);
    }
  };

  if (loading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h1>Estudiantes</h1>
      <button onClick={() => handleCreateStudent({...})}>
        Nuevo Estudiante
      </button>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>RUT</th>
            <th>Nombre</th>
          </tr>
        </thead>
        <tbody>
          {students.map(student => (
            <tr key={student.id}>
              <td>{student.id}</td>
              <td>{student.rut}</td>
              <td>{student.fullName}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

---

### 6. Manejo de Errores

```javascript
const handleError = (error) => {
  if (error.response?.status === 400) {
    return 'Datos inválidos. Revisa el formulario.';
  }
  if (error.response?.status === 404) {
    return 'Recurso no encontrado.';
  }
  if (error.response?.status === 409) {
    return 'Conflicto: El recurso ya existe.';
  }
  if (error.code === 'ECONNABORTED') {
    return 'Timeout: El servidor tardó demasiado.';
  }
  return 'Error desconocido. Intenta nuevamente.';
};
```

---

### 7. Validación de RUT (Cliente)

```javascript
export function validateRUT(rut) {
  const cleanRUT = rut.replace(/\D/g, '');
  if (cleanRUT.length < 8) return false;
  
  const dv = rut.slice(-1).toUpperCase();
  let sum = 0;
  let multiplier = 2;
  
  for (let i = cleanRUT.length - 1; i >= 0; i--) {
    sum += parseInt(cleanRUT[i]) * multiplier;
    multiplier = multiplier === 9 ? 2 : multiplier + 1;
  }
  
  const calculatedDV = 11 - (sum % 11);
  const expected = calculatedDV === 11 ? '0' : calculatedDV === 10 ? 'K' : calculatedDV.toString();
  
  return expected === dv;
}
```

---

---

## Casos de Uso Futuros para BFF

### Fase 1: Dashboard Consolidado

```
GET /api/v1/bff/student/{id}/dashboard
→ Agregar datos de:
  • Perfil estudiante
  • Cursos actuales
  • Evaluaciones próximas
  • Calificaciones recientes
  • Promedios
```

### Fase 2: Reportes

```
GET /api/v1/bff/reports/classroom/{id}/grades
→ Reporte de calificaciones por aula
→ Exportar a PDF/Excel
```

### Fase 3: Notificaciones en Tiempo Real

```
WebSocket: ws://localhost:8085/api/v1/bff/notifications
→ Notificaciones cuando:
  • Se registra una evaluación
  • Se publica una calificación
  • Vence una evaluación
```

---

## Resumen de Endpoints Disponibles

Puedes ver la documentación interactiva en:

- **Student Manager**: http://localhost:8081/docs
- **Classroom Manager**: http://localhost:8084/docs
- **Assessment Manager**: http://localhost:8083/docs

(Swagger UI - OpenAPI 3.0)

---

## Mejores Prácticas

✅ **Valida siempre** antes de crear recursos  
✅ **Usa filtros** en búsquedas (no traer todo)  
✅ **Maneja errores** apropiadamente en Frontend  
✅ **Cachea datos** que no cambian frecuente  
✅ **Implementa debounce** en búsquedas en tiempo real  

---

**¿Preguntas o necesitas más detalles? ¡Abre un issue!**

