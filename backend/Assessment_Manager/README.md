# Assessment Manager - Microservicio de Evaluaciones

**Versión**: 1.0.0  
**Último actualizado**: 19 de mayo de 2026  
**Estado**: Producción  
**Puerto**: 8083

---

## 📋 Descripción

El **Assessment Manager** es responsable de la gestión completa de evaluaciones y calificaciones en el sistema DigitalClassroom. Implementa la lógica de negocio educativa para registrar, validar y almacenar toda la información académica de evaluaciones de estudiantes.

### Responsabilidades Principales

- ✅ Crear y gestionar evaluaciones (encargos, tareas, pruebas)
- ✅ Registrar y modificar calificaciones (notas)
- ✅ Validar estudiantes y cursos antes de registrar calificaciones
- ✅ Implementar reglas de negocio educativo
- ✅ Proteger integridad de datos históricos
- ✅ Comunicación con otros microservicios mediante Feign Client
- ✅ Resiliencia ante fallos con Circuit Breaker

### Stack Tecnológico

| Componente | Versión | Propósito |
|-----------|---------|----------|
| **Spring Boot** | 4.0.6 | Framework principal |
| **Spring Data JPA** | - | ORM y persistencia |
| **Spring Specifications** | - | Consultas dinámicas |
| **MariaDB** | 10.5+ | Base de datos |
| **Feign Client** | - | Cliente HTTP declarativo |
| **Resilience4j** | - | Circuit Breaker y resiliencia |
| **Jakarta Validation** | - | Validación de datos |
| **OpenAPI/Swagger** | 3.0 | Documentación de API |

---

## 🔧 Requisitos Previos

- **Java**: 21 o superior
- **Maven**: 3.8 o superior
- **MariaDB**: 10.5 o superior
- **Git**: Para control de versiones

### Verificar Versiones

```bash
java -version
mvn -v
mariadb --version
```

---

## 📦 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/usuario/DigitalClassroom-project.git
cd DigitalClassroom-project/backend/Assessment_Manager
```

### 2. Configurar la Base de Datos

**Crear base de datos y usuario:**

```sql
CREATE DATABASE assessment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'assessment_user'@'localhost' IDENTIFIED BY 'assessment_password';
GRANT ALL PRIVILEGES ON assessment_db.* TO 'assessment_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar Propiedades de la Aplicación

Editar `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: assessment-manager
  datasource:
    url: jdbc:mariadb://localhost:3306/assessment_db
    username: assessment_user
    password: assessment_password
    driver-class-name: org.mariadb.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true

server:
  port: 8083
  servlet:
    context-path: /api/v1

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000

resilience4j:
  circuitbreaker:
    instances:
      classroom-service:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 5s
        failureRateThreshold: 50

logging:
  level:
    root: INFO
    com.classroom: DEBUG
```

### 4. Compilar e Instalar Dependencias

```bash
# Limpiar builds anteriores
mvn clean

# Compilar y ejecutar tests
mvn install

# Solo compilar (sin tests)
mvn install -DskipTests
```

---

## 🚀 Ejecución

### Opción 1: Maven Spring Boot Plugin

```bash
mvn spring-boot:run
```

**Salida esperada:**
```
...
2026-05-19 10:30:15.123  INFO 12345 --- [main] c.classroom.AssessmentApplication       : Started AssessmentApplication in 8.234 seconds
2026-05-19 10:30:15.234  INFO 12345 --- [main] c.classroom.AssessmentApplication       : Server started on port 8083
```

### Opción 2: Ejecutar JAR Compilado

```bash
mvn clean package

# Ejecutar el JAR
java -jar target/assessment-manager-1.0.0.jar
```

### Opción 3: Docker

```bash
docker build -t assessment-manager:1.0.0 .

docker run -d \
  --name assessment-manager \
  -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/assessment_db \
  -e SPRING_DATASOURCE_USERNAME=assessment_user \
  -e SPRING_DATASOURCE_PASSWORD=assessment_password \
  --link mariadb:mariadb \
  assessment-manager:1.0.0
```

---

## 🧪 Testing

### Ejecutar Todos los Tests

```bash
mvn test
```

### Ejecutar Tests de Clase Específica

```bash
mvn test -Dtest=AssessmentServiceTest
```

### Ejecutar Tests con Cobertura

```bash
mvn clean test jacoco:report
# Reporte en: target/site/jacoco/index.html
```

### Tests Disponibles

- **Unit Tests**: `src/test/java/com/classroom/service/`
- **Integration Tests**: `src/test/java/com/classroom/integration/`
- **Controller Tests**: `src/test/java/com/classroom/controller/`

---

## 📚 API Documentation

Una vez que la aplicación está corriendo, accede a la documentación interactiva:

**Swagger UI**: http://localhost:8083/api/v1/swagger-ui.html

**OpenAPI JSON**: http://localhost:8083/api/v1/v3/api-docs

### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/assessments` | Obtener todas las evaluaciones |
| POST | `/assessments` | Crear nueva evaluación |
| GET | `/assessments/{id}` | Obtener evaluación por ID |
| PUT | `/assessments/{id}` | Actualizar evaluación |
| DELETE | `/assessments/{id}` | Eliminar evaluación |
| POST | `/grades` | Registrar calificación |
| GET | `/grades/student/{studentId}` | Obtener calificaciones del estudiante |
| GET | `/grades/assessment/{assessmentId}` | Obtener calificaciones de evaluación |

---

## 🔍 Verificar Salud del Servicio

```bash
curl http://localhost:8083/api/v1/actuator/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "circuitBreakers": {"status": "UP"}
  }
}
```

---

## 🐛 Solución de Problemas

### Error: Puerto 8083 ya está en uso

```bash
# En Windows
netstat -ano | findstr :8083
taskkill /PID <PID> /F

# En macOS/Linux
lsof -i :8083
kill -9 <PID>
```

### Error: Conexión a Base de Datos Rechazada

1. Verificar que MariaDB está corriendo:
   ```bash
   # En Windows
   Get-Service | Where-Object {$_.Name -like "*mariadb*"} | Start-Service
   ```

2. Verificar credenciales en `application.yml`

3. Verificar puerto MariaDB (por defecto 3306):
   ```bash
   mysql -h localhost -u assessment_user -p assessment_db
   ```

### Error: Compilación Fallida

```bash
# Limpiar y reconstruir
mvn clean install -U

# Reconstruir índices de dependencias
mvn dependency:resolve -U
```

---

## 📖 Estructura del Proyecto

```
Assessment_Manager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/classroom/
│   │   │       ├── controller/     # Controladores REST
│   │   │       ├── service/        # Lógica de negocio
│   │   │       ├── repository/     # Acceso a datos
│   │   │       ├── entity/         # Entidades JPA
│   │   │       ├── dto/            # Data Transfer Objects
│   │   │       ├── exception/      # Manejo de excepciones
│   │   │       └── config/         # Configuración
│   │   └── resources/
│   │       ├── application.yml     # Configuración principal
│   │       └── db/                 # Scripts SQL
│   └── test/
│       └── java/                   # Tests unitarios e integración
├── pom.xml                          # Configuración Maven
├── Dockerfile                       # Configuración Docker
└── README.md                        # Este archivo
```

---

## 🔐 Seguridad

- ✅ Validación de entrada en todos los endpoints
- ✅ Manejo seguro de excepciones sin exponer información sensible
- ✅ Circuit Breaker para proteger de servicios degradados
- ✅ Timeouts configurables para evitar bloqueos
- ✅ Logging seguro sin datos sensibles

---

## 📝 Variables de Entorno

```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/assessment_db
SPRING_DATASOURCE_USERNAME=assessment_user
SPRING_DATASOURCE_PASSWORD=assessment_password

# Aplicación
SERVER_PORT=8083
SPRING_PROFILES_ACTIVE=production

# Otros microservicios
CLASSROOM_SERVICE_URL=http://localhost:8082
STUDENT_SERVICE_URL=http://localhost:8081
```

---

## 🤝 Contribuir

1. Crear rama siguiendo GitFlow: `feature/nombre-feature`
2. Hacer commit con mensajes descriptivos
3. Ejecutar tests antes de push
4. Crear Pull Request hacia `develop`
5. Esperar revisión antes de merge

---

## 📄 Licencia

Este proyecto es parte de DigitalClassroom y está bajo licencia educativa.

---

## 👥 Soporte

Para problemas o preguntas:
1. Revisar los logs: `tail -f target/app.log`
2. Consultar documentación de endpoints: `/api/v1/swagger-ui.html`
3. Contactar al equipo de desarrollo

**Última verificación**: 19 de mayo de 2026 ✓

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

## Documentacion de la API

### Swagger UI

```
http://localhost:8083/docs
```

---

## Endpoints Principales

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

## Monitoreo

```
GET /actuator/health
GET /actuator/metrics
GET /actuator/prometheus
GET /actuator/health/circuitbreakers
```

---

## Ejemplos de Uso

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

## Notas Importantes

- ⚠️ El borrado de evaluaciones está protegido por reglas de negocio
- ⚠️ Los puntajes deben estar en el rango chileno (1.0-7.0)
- ⚠️ Todas las validaciones con otros servicios usan Circuit Breaker
- Las fechas se almacenan en formato LocalDate (ISO 8601)
- Las calificaciones generan automaticamente su fecha de creacion

---

**Última actualización**: 16 de Mayo de 2026

