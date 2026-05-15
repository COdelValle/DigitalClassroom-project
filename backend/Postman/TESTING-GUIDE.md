# Digital Classroom - Guía Completa de Testing

## 📋 Índice

1. [Setup Inicial](#setup-inicial)
2. [Pruebas por Microservicio](#pruebas-por-microservicio)
3. [Escenarios de Prueba](#escenarios-de-prueba)
4. [Validaciones Esperadas](#validaciones-esperadas)
5. [Checklist de Testing](#checklist-de-testing)

---

## Setup Inicial

### Requisitos Previos

1. ✅ Java 21 instalado
2. ✅ Maven 3.9+
3. ✅ Postman instalado
4. ✅ Base de datos MariaDB/MySQL ejecutándose
5. ✅ Todos los microservicios compilados

### Pasos de Preparación

#### 1. Importar la Colección en Postman

```
1. Abre Postman
2. Haz clic en Import
3. Selecciona: DigitalClassroom-API-Collection.postman_collection.json
4. Selecciona Import
```

#### 2. Importar Environment

```
1. Haz clic en "Manage Environments"
2. Haz clic en Import
3. Selecciona: Digital-Classroom-Environment.postman_environment.json
4. Cierra y selecciona el environment en el dropdown
```

#### 3. Levantar los Microservicios

En terminales separadas:

```bash
# Terminal 1 - Student Manager
cd backend
mvn clean install
mvn spring-boot:run -pl Student_Manager

# Terminal 2 - Classroom Manager
mvn spring-boot:run -pl Classroom_Manager

# Terminal 3 - Assessment Manager
mvn spring-boot:run -pl Assessment_Manager
```

Verifica que todos los servicios estén listos:
- Student Manager: `http://localhost:8081/api/v1/students`
- Classroom Manager: `http://localhost:8082/api/v1/classroom`
- Assessment Manager: `http://localhost:8083/api/v1/assessments`

---

## Pruebas por Microservicio

### 1. Student Manager (Puerto 8081)

#### 1.1 Crear Estudiante

**Request:**
```json
POST /api/v1/students
Content-Type: application/json

{
  "rut": "12.345.678-9",
  "firstName": "Juan",
  "middleName": "Carlos",
  "lastName": "Pérez García",
  "birthDate": "2010-05-15",
  "allergies": ["Ninguna"],
  "legalRepresentatives": [
    {
      "rut": "10.123.456-7",
      "fullName": "María García",
      "email": "maria@example.com",
      "phoneNumber": ["+56 9 1111 2222"],
      "relationship": "Madre"
    }
  ]
}
```

**Validaciones:**
- ✅ Status Code: 201 (Created)
- ✅ Response incluye `id` (capturar como `student_id`)
- ✅ Response incluye todos los campos del estudiante
- ✅ Response incluye representantes legales

**Casos de Error:**

```json
❌ RUT Inválido: "invalid-rut"
❌ RUT Duplicado: "12.345.678-9" (si ya existe)
❌ Alergias vacías: []
❌ Sin representantes: []
❌ Email inválido en representante
```

#### 1.2 Listar Estudiantes

**Request:**
```
GET /api/v1/students
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Response es un array
- ✅ Cada item tiene: `id`, `rut`, `fullName`
- ✅ Si hay estudiantes, el array no está vacío

#### 1.3 Obtener Perfil (Vista Profesor)

**Request:**
```
GET /api/v1/students/{id}/profile
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Response incluye: `id`, `rut`, `fullName`, `allergies`, `emergencyContacts`
- ✅ Response NO incluye detalles sensibles
- ✅ Contactos de emergencia tienen: `name`, `phoneNumbers`, `relationship`

#### 1.4 Obtener Detalles Completos (Vista Admin)

**Request:**
```
GET /api/v1/students/{id}/full
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Response incluye todos los campos: `id`, `rut`, `firstName`, `middleName`, `lastName`, `birthDate`, `allergies`, `legalRepresentatives`
- ✅ Información completa del representante incluida

#### 1.5 Verificar Existencia

**Request:**
```
GET /api/v1/students/{id}/exists
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Response: `true` si existe, `false` si no
- ✅ Para ID válido: `true`
- ✅ Para ID inválido (9999): `false`

#### 1.6 Actualizar Estudiante

**Request:**
```json
PUT /api/v1/students/{id}
Content-Type: application/json

{
  "rut": "12.345.678-9",
  "firstName": "Juan Updated",
  "middleName": "Carlos",
  "lastName": "Pérez García",
  "birthDate": "2010-05-15",
  "allergies": ["Maní"],
  "legalRepresentatives": [
    {
      "rut": "10.123.456-7",
      "fullName": "María García",
      "email": "maria.updated@example.com",
      "phoneNumber": ["+56 9 1111 2222"],
      "relationship": "Madre"
    }
  ]
}
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ datos actualizado en respuesta
- ✅ GET confirma cambios persistidos
- ✅ RUT no se puede cambiar

#### 1.7 Eliminar Estudiante

**Request:**
```
DELETE /api/v1/students/{id}
```

**Validaciones:**
- ✅ Status Code: 204 (No Content)
- ✅ GET confirma que ya no existe
- ✅ exists retorna `false`

---

### 2. Classroom Manager (Puerto 8082)

#### 2.1 Crear Aula

**Request:**
```json
POST /api/v1/classroom
{
  "name": "4º Básico A",
  "year": 2026,
  "capacity": 30
}
```

**Validaciones:**
- ✅ Status Code: 201
- ✅ Response incluye `id` (capturar como `classroom_id`)
- ✅ Response incluye todos los datos

#### 2.2 Buscar Aulas

**Request:**
```
GET /api/v1/classroom/search?name=4º&year=2026
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Retorna aulas coincidentes
- ✅ Búsqueda parcial funciona (4º encuentra 4º Básico)

#### 2.3 Crear Asignatura

**Request:**
```json
POST /api/v1/subjects
{
  "name": "Matemática",
  "area": "Ciencias Exactas",
  "hours": 5
}
```

**Validaciones:**
- ✅ Status Code: 201
- ✅ Response incluye `id` (capturar como `subject_id`)

#### 2.4 Buscar Asignaturas

**Request:**
```
GET /api/v1/subjects/search?name=Matemática
GET /api/v1/subjects/search?area=Humanidades
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Retorna resultados coincidentes

#### 2.5 Crear Curso (Asignatura por Aula)

**Request:**
```json
POST /api/v1/courses
{
  "classroomId": 1,
  "subjectId": 1,
  "teacher": "Prof. González",
  "semester": "1"
}
```

**Validaciones:**
- ✅ Status Code: 201
- ✅ Response incluye `id` (capturar como `course_id`)
- ✅ Vincula correctamente aula y asignatura

#### 2.6 Verificar Curso Existe

**Request:**
```
GET /api/v1/courses/{id}/exists
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Response: `true` para curso existente
- ✅ Response: `false` para curso no existente

---

### 3. Assessment Manager (Puerto 8083)

#### 3.1 Crear Evaluación

**Request:**
```json
POST /api/v1/assessments
{
  "title": "Fracciones",
  "courseId": 1,
  "examDate": "2026-06-18"
}
```

**Validaciones:**
- ✅ Status Code: 201
- ✅ Response incluye `id` (capturar como `assessment_id`)
- ✅ courseId validado contra Course Manager
- ✅ Response incluye fecha de examen

**Casos de Error:**
```
❌ courseId inválido (9999): Error validación
❌ courseId que no existe: CircuitBreaker activa fallback
```

#### 3.2 Buscar Evaluaciones

**Request:**
```
GET /api/v1/assessments/search?courseId=1
GET /api/v1/assessments/search?title=Fracciones
GET /api/v1/assessments/search?examDate=2026-06-18
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Retorna evaluaciones coincidentes

#### 3.3 Crear Calificación

**Request:**
```json
POST /api/v1/grades
{
  "studentId": 1,
  "assessmentId": 1,
  "score": 7.5
}
```

**Validaciones:**
- ✅ Status Code: 201
- ✅ Response incluye `id` (capturar como `grade_id`)
- ✅ Score debe estar entre 1.0 y 7.0 (escala chilena)
- ✅ Response incluye todos los datos

**Casos de Error:**
```
❌ studentId inválido: Error validación
❌ assessmentId inválido: Error
❌ Score > 7.0: Error validación
❌ Score < 1.0: Error validación
```

#### 3.4 Buscar Calificaciones

**Request:**
```
GET /api/v1/grades/search?studentId=1
GET /api/v1/grades/search?minScore=6.0&maxScore=8.0
GET /api/v1/grades/search?minScore=8.0
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Retorna calificaciones coincidentes
- ✅ Rango de búsqueda funciona correctamente

#### 3.5 Actualizar Calificación

**Request:**
```json
PUT /api/v1/grades/{id}
{
  "score": 7.8
}
```

**Validaciones:**
- ✅ Status Code: 200
- ✅ Score actualizado
- ✅ GET confirma cambios

---

## Escenarios de Prueba

### Escenario 1: Flujo Completo Lineal

```
1. Crear Aula (4º Básico A) → Guardar classroom_id
2. Crear Asignatura (Matemática) → Guardar subject_id
3. Crear Curso (Aula + Asignatura) → Guardar course_id
4. Crear Estudiante → Guardar student_id
5. Crear Evaluación (Curso + Fecha) → Guardar assessment_id
6. Crear Calificación (Estudiante + Evaluación + Score) → Guardar grade_id
7. Verificar todos los datos con GET
```

### Escenario 2: Múltiples Estudiantes y Evaluaciones

```
1. Crear 3 estudiantes diferentes
2. Crear 2 aulas
3. Crear 3 asignaturas
4. Crear 6 cursos (2 aulas × 3 asignaturas)
5. Crear 4 evaluaciones
6. Asignar calificaciones a todos los estudiantes para cada evaluación
7. Buscar por diferentes criterios
```

### Escenario 3: Validación de Restricciones

```
1. Intentar crear estudiante con RUT inválido ❌
2. Intentar crear estudiante con RUT duplicado ❌
3. Intentar crear evaluación con courseId inválido ❌
4. Intentar crear calificación con score > 7.0 ❌
5. Intentar acceder a recursos no existentes ❌
```

### Escenario 4: Búsqueda y Filtrado

```
1. Crear múltiples recursos
2. Buscar por cada parámetro disponible
3. Buscar con parámetros combinados
4. Verificar que se retornan resultados correctos
5. Validar que no aparecen recursos que no coinciden
```

---

## Validaciones Esperadas

### Respuestas Exitosas (2xx)

| Endpoint | Método | Status | Respuesta |
|----------|--------|--------|-----------|
| /students | POST | 201 | StudentProfileResponseDTO |
| /students | GET | 200 | List<StudentShortResponseDTO> |
| /students/{id}/profile | GET | 200 | StudentProfileResponseDTO |
| /students/{id}/full | GET | 200 | StudentFullResponseDTO |
| /students/{id}/exists | GET | 200 | boolean |
| /students/{id} | PUT | 200 | StudentFullResponseDTO |
| /students/{id} | DELETE | 204 | (vacío) |

### Respuestas de Error (4xx, 5xx)

| Error | Status | Causa |
|-------|--------|-------|
| Resource Not Found | 404 | ID no existe |
| Validation Error | 400 | Datos inválidos |
| Conflict | 409 | RUT duplicado |
| Service Unavailable | 503 | Microservicio no disponible |

---

## Checklist de Testing

### ✅ Student Manager

- [ ] Crear estudiante válido
- [ ] Crear estudiante con RUT inválido (error)
- [ ] Crear estudiante con RUT duplicado (error)
- [ ] Crear sin alergias (error)
- [ ] Listar todos (GET)
- [ ] Obtener perfil (profesor view)
- [ ] Obtener completo (admin view)
- [ ] Verificar existencia (true/false)
- [ ] Actualizar estudiante
- [ ] Actualizar con datos inválidos (error)
- [ ] Eliminar estudiante
- [ ] Verificar que fue eliminado
- [ ] Acceder a no existente (404)

### ✅ Classroom Manager - Aulas

- [ ] Crear aula
- [ ] Obtener aula por ID
- [ ] Buscar por nombre
- [ ] Buscar por año
- [ ] Buscar por ambos parámetros
- [ ] Actualizar aula
- [ ] Eliminar aula

### ✅ Classroom Manager - Asignaturas

- [ ] Crear asignatura
- [ ] Obtener asignatura por ID
- [ ] Buscar por nombre
- [ ] Buscar por área
- [ ] Actualizar asignatura
- [ ] Eliminar asignatura

### ✅ Classroom Manager - Cursos

- [ ] Crear curso
- [ ] Obtener curso por ID
- [ ] Buscar por classroom
- [ ] Buscar por profesor
- [ ] Buscar por semestre
- [ ] Verificar existencia
- [ ] Actualizar curso
- [ ] Eliminar curso

### ✅ Assessment Manager - Evaluaciones

- [ ] Crear evaluación con curso válido
- [ ] Crear evaluación con curso inválido (error)
- [ ] Obtener evaluación por ID
- [ ] Buscar por curso
- [ ] Buscar por título
- [ ] Buscar por fecha
- [ ] Actualizar evaluación
- [ ] Eliminar evaluación

### ✅ Assessment Manager - Calificaciones

- [ ] Crear calificación válida
- [ ] Crear con score > 7.0 (error)
- [ ] Crear con score < 1.0 (error)
- [ ] Obtener calificación por ID
- [ ] Buscar por estudiante
- [ ] Buscar por rango de score
- [ ] Actualizar calificación
- [ ] Eliminar calificación

### ✅ Flujos Integrados

- [ ] Crear estudiante → Crear evaluación → Crear calificación
- [ ] Múltiples estudiantes con diferentes calificaciones
- [ ] Búsquedas retornan resultados correctos

### ✅ Casos de Error

- [ ] 404 Not Found para resources no existentes
- [ ] 400 Bad Request para datos inválidos
- [ ] 409 Conflict para duplicados
- [ ] Mensajes de error claros

---

## Notas Importantes

### Circuit Breaker

El servicio de Assessment usa Circuit Breaker para validar cursos:

- Si Course Manager está down, la operación falla con error de servicio
- Esperar a que Course Manager esté listo antes de crear evaluaciones

### RUT Validación

- Formato: XX.XXX.XXX-X
- Se valida módulo 11
- Ejemplos válidos: 12.345.678-9, 15.987.654-3

### Fechas

- Formato: YYYY-MM-DD
- Birth Date debe ser en el pasado (@Past)
- Exam Date debe ser en el futuro

### Logs Útiles

Ver logs en cada servicio para:
- ✅ Confirmación de operaciones
- ✅ Errores de validación
- ✅ Circuit Breaker activaciones
- ✅ Queries de base de datos

---

## Próximas Mejoras

- [ ] Tests de carga
- [ ] Pruebas de concurrencia
- [ ] Autenticación/Autorización
- [ ] Caché testing
- [ ] Rollback automático de datos
- [ ] Tests de performan

ce

---

**Última actualización:** 2026-05-15
**Autor:** Development Team

