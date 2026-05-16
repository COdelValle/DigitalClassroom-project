# Assessment Manager Microservicio

**Versión**: 1.0.0  
**Último actualizado**: 16 de Mayo de 2026  
**Estado**: ✅ Producción

---

## 📋 Descripción

El **Assessment Manager** gestiona evaluaciones y calificaciones en el sistema. Es responsable de todo lo relacionado con pruebas, tareas y notas de los estudiantes.

**Responsabilidades**:
- ✅ Crear y gestionar evaluaciones (encargos)
- ✅ Registrar y modificar calificaciones (notas)
- ✅ Validar estudiantes y cursos
- ✅ Implementar reglas de negocio educativo
- ✅ Proteger datos históricos

**Stack Tecnológico**:
- Spring Boot 4.0.6
- Spring Data JPA + Specifications
- MariaDB
- Feign Client (validar con otros servicios)
- Resilience4j Circuit Breaker
- Jakarta Validation
- OpenAPI/Swagger

---

## 🚀 Inicio Rápido

### Requisitos

- Java 21+
- MariaDB 10.5+
- Maven 3.8+

### Instalación

```bash
cd backend/Assessment_Manager

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

**Puerto por defecto**: `8083`

### Base de Datos

Se crea automáticamente. Configuración:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/assessment_manager_db
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

---

## 📚 Documentación de la API

### Swagger UI

```
http://localhost:8083/docs
```

---

## 🎯 Endpoints Principales

### Assessment Controller

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/assessments/{id}` | Obtener evaluación |
| GET | `/api/v1/assessments/search` | Buscar evaluaciones |
| POST | `/api/v1/assessments` | Crear evaluación |
| PUT | `/api/v1/assessments/{id}` | Actualizar evaluación |
| DELETE | `/api/v1/assessments/{id}` | Eliminar evaluación |

### Grade Controller

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/grades/{id}` | Obtener calificación |
| GET | `/api/v1/grades/search` | Buscar calificaciones |
| POST | `/api/v1/grades` | Crear calificación |
| PUT | `/api/v1/grades/{id}` | Actualizar calificación |
| DELETE | `/api/v1/grades/{id}` | Eliminar calificación |

---

## 📦 Modelos de Datos

### Assessment Entity

```java
{
  id: Long,                  // ID único
  title: String,             // Título de evaluación
  courseId: Long,            // ID de la relación curso
  examDate: LocalDate,       // Fecha (YYYY-MM-DD)
  grades: List<Grade>,       // Calificaciones asociadas
  isGraded: boolean          // Tiene calificaciones
}
```

### Grade Entity

```java
{
  id: Long,                    // ID único
  studentId: Long,             // ID del estudiante
  score: Double,               // Puntaje (1.0-7.0 chileno)
  registrationDate: LocalDateTime  // Fecha/hora creación (automática)
}
```

**Validación de Puntaje (@ChileanGrade)**:
- Rango: 1.0 - 7.0
- Ejemplo válido: 5.5, 6.0, 7.0
- Ejemplo inválido: 0.5, 8.0, -1.0

---

## 🔗 Integraciones

### Validaciones Externas

**1. Validar Curso (Classroom Manager)**:

```
GET /api/v1/courses/{courseId}/exists
```

Usado en:
- Crear evaluación
- Actualizar evaluación

**2. Validar Estudiante (Student Manager)**:

```
GET /api/v1/students/{studentId}/exists
```

Usado en:
- Crear evaluación con calificaciones
- Crear calificación independiente
- Actualizar calificación

### Circuit Breaker Config

```properties
# Para Estudiantes
resilience4j.circuitbreaker.instances.studentServiceCB.slidingWindowSize=10
resilience4j.circuitbreaker.instances.studentServiceCB.failureRateThreshold=50
resilience4j.circuitbreaker.instances.studentServiceCB.waitDurationInOpenState=20s

# Para Académico (Classroom)
resilience4j.circuitbreaker.instances.academicServiceCB.slidingWindowSize=6
resilience4j.circuitbreaker.instances.academicServiceCB.failureRateThreshold=40
resilience4j.circuitbreaker.instances.academicServiceCB.waitDurationInOpenState=15s
```

---

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Con cobertura
mvn test jacoco:report
```

---

## ⚙️ Configuración Avanzada

**application.properties**:

```properties
spring.application.name=Assessment_Manager
server.port=8083
spring.datasource.url=jdbc:mariadb://localhost:3306/assessment_manager_db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Feign - URLs de otros servicios
external.services.student-url=http://localhost:8081
external.services.academic-url=http://localhost:8084

# Circuit Breaker configuración
resilience4j.circuitbreaker.instances.studentServiceCB.registerHealthIndicator=true
resilience4j.circuitbreaker.instances.academicServiceCB.registerHealthIndicator=true

# Swagger
springdoc.swagger-ui.path=/docs
springdoc.swagger-ui.operationsSorter=method
```

---

## 🔒 Seguridad y Reglas de Negocio

### Restricciones de Eliminación

**No se puede eliminar evaluación si**:
1. Es del año actual (current year) Y su estado es `isGraded` (tiene notas)
2. Es de años anteriores (datos históricos)

**Razón**: Proteger integridad de datos académicos

### Validación de Estudiantes

Todos los `studentId` en calificaciones se validan con Student Manager mediante Circuit Breaker

### Rango de Puntaje

Solo se aceptan puntajes en el sistema chileno (1.0-7.0)

---

## 🐛 Troubleshooting

### Error: "Evaluación no encontrada"

**Causa**: ID inexistente  
**Solución**: Verificar que el ID sea correcto

### Error: "Estudiante no existe o el servicio de alumnos no responde"

**Causa**: 
- Student Manager está caído
- Estudiante no existe
- Circuit Breaker abierto

**Solución**: Verificar Student Manager está arriba

### Error: "No existe una relación entre la asignatura y la clase"

**Causa**: Curso (courseId) no existe  
**Solución**: Crear curso primero en Classroom Manager

### Error: "Prohibido eliminar: La evaluación pertenece al año actual y tiene notas"

**Causa**: Intenta eliminar evaluación con notas del año actual  
**Solución**: Eliminar calificaciones primero OR esperar a próximo año

### Error: "Puntaje inválido"

**Causa**: Score fuera del rango 1.0-7.0  
**Solución**: Usar puntaje válido (ej: 5.5, 6.0, 7.0)

---

## 📊 Monitoreo

```
GET /actuator/health
GET /actuator/metrics
GET /actuator/prometheus
GET /actuator/health/circuitbreakers
```

---

## 📝 Ejemplos de Uso

### Crear Evaluación con Calificaciones

```bash
curl -X POST http://localhost:8083/api/v1/assessments \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Examen Parcial",
    "courseId": 1,
    "examDate": "2024-06-15",
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
  }'
```

### Crear Evaluación sin Calificaciones

```bash
curl -X POST http://localhost:8083/api/v1/assessments \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Examen Final",
    "courseId": 1,
    "examDate": "2024-06-20"
  }'
```

### Registrar Calificación

```bash
curl -X POST http://localhost:8083/api/v1/grades \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "assessmentId": 1,
    "score": 6.5
  }'
```

### Buscar Calificaciones de un Estudiante

```bash
curl "http://localhost:8083/api/v1/grades/search?studentId=1"
```

### Buscar Evaluaciones de un Curso

```bash
curl "http://localhost:8083/api/v1/assessments/search?courseId=1"
```

### Buscar por Rango de Puntaje

```bash
curl "http://localhost:8083/api/v1/grades/search?minScore=5.0&maxScore=7.0"
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

## 📋 Notas Importantes

- ⚠️ El borrado de evaluaciones está protegido por reglas de negocio
- ⚠️ Los puntajes deben estar en el rango chileno (1.0-7.0)
- ⚠️ Todas las validaciones con otros servicios usan Circuit Breaker
- ✅ Las fechas se almacenan en formato LocalDate (ISO 8601)
- ✅ Las calificaciones generan automáticamente su fecha de creación

---

**Última actualización**: 16 de Mayo de 2026

