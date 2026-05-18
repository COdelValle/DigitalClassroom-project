# Classroom Manager Microservicio

**Versión**: 1.0.0  
**Último actualizado**: 16 de Mayo de 2026  
**Estado**: Produccion

---

## Descripcion

El **Classroom Manager** gestiona la estructura académica de la plataforma: aulas, asignaturas y la relación entre ambas (cursos). Es el corazón de la organización académica.

**Responsabilidades**:
- Crear y gestionar aulas
- Crear y gestionar asignaturas
- Vincular asignaturas a aulas con profesores
- Validar existencia de recursos
- Mantener integridad referencial

**Stack Tecnológico**:
- Spring Boot 4.0.6
- Spring Data JPA + Specifications
- MariaDB
- Feign Client (validación con Student Manager)
- Resilience4j Circuit Breaker
- OpenAPI/Swagger

---

## Inicio Rapido

### Requisitos

- Java 21+
- MariaDB 10.5+
- Maven 3.8+

### Instalación

```bash
cd backend/Classroom_Manager

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

**Puerto por defecto**: `8084`

### Base de Datos

Se crea automáticamente. Configuración:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/classroom_manager_db
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

---

## Documentacion de la API

### Swagger UI

```
http://localhost:8084/docs
```

---

## Endpoints Principales

### Classroom Controller

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/classroom/{id}` | Obtener aula |
| GET | `/api/v1/classroom/search` | Buscar aulas |
| POST | `/api/v1/classroom` | Crear aula |
| PUT | `/api/v1/classroom/{id}` | Actualizar aula |
| DELETE | `/api/v1/classroom/{id}` | Eliminar aula |

### Course Controller

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/courses/{id}` | Obtener curso |
| GET | `/api/v1/courses/search` | Buscar cursos |
| POST | `/api/v1/courses` | Crear curso |
| PUT | `/api/v1/courses/{id}` | Actualizar curso |
| DELETE | `/api/v1/courses/{id}` | Eliminar curso |
| GET | `/api/v1/courses/{id}/exists` | Verificar existencia |

### Subject Controller

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/subjects/{id}` | Obtener asignatura |
| GET | `/api/v1/subjects/search` | Buscar asignaturas |
| POST | `/api/v1/subjects` | Crear asignatura |
| PUT | `/api/v1/subjects/{id}` | Actualizar asignatura |
| DELETE | `/api/v1/subjects/{id}` | Eliminar asignatura |

---

## 📦 Modelos de Datos

### Classroom Entity

```java
{
  id: Long,                  // ID único
  code: String,              // Código único (ej: "4A-2024")
  name: String,              // Nombre (ej: "4to A")
  schoolYear: Integer,       // Año escolar
  studentIds: List<Long>     // IDs de estudiantes
}
```

### Subject Entity

```java
{
  id: Long,           // ID único
  name: String,       // Nombre único
  area: String,       // Área (ej: "Ciencias Exactas")
  isActive: Boolean   // Está activa
}
```

### Course Entity (Relación)

```java
{
  id: Long,
  subject: Subject,           // Referencia a asignatura
  classroom: Classroom,       // Referencia a aula
  schoolYear: Integer,        // Año escolar
  semester: String,           // Semestre
  teacherName: String         // Profesor
}
```

---

## 🔗 Integraciones

### Validaciones Externas

**Consulta a Student Manager**:
```
GET /api/v1/students/{id}/exists
```

Usado cuando:
- Crear aula (validar estudiantes)
- Actualizar aula (validar nuevos estudiantes)

**Circuit Breaker**: Si Student Manager falla, retorna error apropiado

---

## Testing

```bash
# Ejecutar tests
mvn test

# Con cobertura
mvn test jacoco:report
```

---

## Configuracion Avanzada

**application.properties**:

```properties
spring.application.name=Classroom_Manager
server.port=8084
spring.datasource.url=jdbc:mariadb://localhost:3306/classroom_manager_db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Feign para validar estudiantes
external.services.student-url=http://localhost:8081

# Circuit Breaker
resilience4j.circuitbreaker.instances.studentServiceCB.slidingWindowSize=10
resilience4j.circuitbreaker.instances.studentServiceCB.failureRateThreshold=50
resilience4j.circuitbreaker.instances.studentServiceCB.waitDurationInOpenState=20s

# Swagger
springdoc.swagger-ui.path=/docs
```

---

## Monitoreo

```
GET /actuator/health
GET /actuator/metrics
GET /actuator/prometheus
```

---

## 🔒 Seguridad

- Validacion de entrada (Jakarta Validation)
- Validacion de unicidad (codigo, nombre de asignatura)
- Circuit Breaker para comunicacion con otros servicios
- OAuth2 configurado

---

## 🐛 Troubleshooting

### Error: "El código de aula ya existe"

**Causa**: Código duplicado  
**Solución**: Usar código único

### Error: "El estudiante con ID X no existe"

**Causa**: Student Manager respondió no  
**Solución**: Crear el estudiante primero en Student Manager

### Error: "No se puede eliminar el aula porque tiene cursos activos"

**Causa**: Aula tiene cursos vinculados  
**Solución**: Eliminar cursos primero

### Error: "Ya existe un curso configurado para esta Asignatura en esta aula"

**Causa**: La combinación asignatura-aula-año-semestre ya existe  
**Solución**: Usar una combinación única

---

## Ejemplos de Uso

### Crear Asignatura

```bash
curl -X POST http://localhost:8084/api/v1/subjects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Matemáticas",
    "area": "Ciencias Exactas"
  }'
```

### Crear Aula

```bash
curl -X POST http://localhost:8084/api/v1/classroom \
  -H "Content-Type: application/json" \
  -d '{
    "code": "4A-2024",
    "name": "4to A",
    "schoolYear": 2024,
    "studentIds": [1, 2, 3]
  }'
```

### Vincular Asignatura a Aula

```bash
curl -X POST http://localhost:8084/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{
    "subjectId": 1,
    "classroomId": 1,
    "schoolYear": 2024,
    "semester": "Primer Semestre",
    "teacherName": "Prof. Juan Pérez"
  }'
```

### Buscar Cursos de un Aula

```bash
curl "http://localhost:8084/api/v1/courses/search?classroomId=1"
```

---

## 🤝 Contribuciones

1. Crea una rama: `git checkout -b feature/nombre`
2. Haz commit: `git commit -m "Descripción"`
3. Push: `git push origin feature/nombre`
4. Abre un Pull Request

---

## 📞 Soporte

Para problemas:
- Abre una issue en el repositorio
- Contacta al equipo de backend

---

**Última actualización**: 16 de Mayo de 2026

