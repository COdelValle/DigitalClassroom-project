# BFF Web (Backend For Frontend) Microservicio

**Versión**: 0.1.0 (En Desarrollo)  
**Último actualizado**: 16 de Mayo de 2026  
**Estado**: 🚧 EN DESARROLLO

---

## 📋 Descripción

El **BFF Web** es un microservicio en desarrollo que actuará como punto centralizado entre el Frontend y los demás microservicios. Su propósito es:

- ✅ Agregar datos de múltiples microservicios
- ✅ Transformar datos para el consumo del Frontend
- ✅ Implementar lógica de negocio compleja
- ✅ Cachear datos para mejorar rendimiento
- ✅ Reducir la complejidad en el Frontend
- ✅ Manejo centralizado de errores

**Stack Tecnológico**:
- Spring Boot 4.0.6
- OpenFeign Client
- Resilience4j Circuit Breaker
- Spring Security + OAuth2
- OpenAPI/Swagger

---

## 🚀 Inicio Rápido

### Requisitos

- Java 21+
- Maven 3.8+
- Los demás microservicios deben estar corriendo

### Instalación

```bash
cd backend/BFF_Web

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

**Puerto por defecto**: `8085` (configurable)

### Base de Datos

❌ Este microservicio **NO tiene base de datos** - solo consume APIs de otros servicios

---

## 📚 Documentación de la API

### Swagger UI (cuando esté implementado)

```
http://localhost:8085/docs
```

---

## 🏗️ Arquitectura Actual

```
bffweb/
├── BffWebApplication.java
├── controller/
│   └── DashboardController.java (VACÍO)
├── client/
│   ├── StudentClient.java (VACÍO)
│   ├── ClassroomClient.java (VACÍO)
│   └── AssessmentClient.java (VACÍO)
├── service/
│   ├── DashboardService.java (VACÍO)
│   └── impl/
├── config/
│   ├── FeignConfig.java (VACÍO)
│   └── SecurityConfig.java (VACÍO)
└── dto/
    ├── request/ (VACÍO)
    └── response/ (VACÍO)
```

---

## 🔗 Clientes Feign (Próxima Implementación)

Los clientes Feign conectarán con:

```java
// StudentClient → Student Manager (8081)
@FeignClient(name = "student-service", url = "http://localhost:8081/api/v1")
public interface StudentClient {
    @GetMapping("/students/{id}/profile")
    StudentProfileResponseDTO getProfile(@PathVariable Long id);
    
    @GetMapping("/students")
    List<StudentShortResponseDTO> getAllStudents();
}

// ClassroomClient → Classroom Manager (8084)
@FeignClient(name = "classroom-service", url = "http://localhost:8084/api/v1")
public interface ClassroomClient {
    @GetMapping("/classroom/{id}")
    ClassroomResponseDTO getClassroom(@PathVariable Long id);
    
    @GetMapping("/courses/search")
    List<CourseResponseDTO> searchCourses(@RequestParam Long classroomId);
}

// AssessmentClient → Assessment Manager (8083)
@FeignClient(name = "assessment-service", url = "http://localhost:8083/api/v1")
public interface AssessmentClient {
    @GetMapping("/assessments/search")
    List<AssessmentResponseDTO> searchAssessments(@RequestParam Long courseId);
    
    @GetMapping("/grades/search")
    List<GradeResponseDTO> searchGrades(@RequestParam Long studentId);
}
```

---

## 📋 Servicios Planificados

### 1. DashboardService

Agregará datos para dashboards de diferentes roles:

```java
@Service
public class DashboardService {
    
    // Dashboard del Estudiante
    public StudentDashboardDTO getStudentDashboard(Long studentId) {
        // 1. Obtener info del estudiante
        // 2. Obtener sus cursos
        // 3. Obtener evaluaciones próximas
        // 4. Obtener calificaciones recientes
        // 5. Calcular promedio
        // 6. Agregar todo
    }
    
    // Dashboard del Profesor
    public TeacherDashboardDTO getTeacherDashboard(Long teacherId) {
        // 1. Obtener cursos asignados
        // 2. Obtener estudiantes por curso
        // 3. Obtener evaluaciones pendientes
        // 4. Obtener calificaciones sin registrar
    }
    
    // Dashboard Administrativo
    public AdminDashboardDTO getAdminDashboard() {
        // 1. Total estudiantes
        // 2. Total aulas
        // 3. Total asignaturas
        // 4. Éxito/fracaso general
        // 5. Rendimiento por aula
    }
}
```

---

## 📋 Endpoints Planificados

**Fase 1 - Dashboards**:

```http
GET /api/v1/bff/dashboard/student/{id}
GET /api/v1/bff/dashboard/teacher/{id}
GET /api/v1/bff/dashboard/admin
```

**Fase 2 - Reportes**:

```http
GET /api/v1/bff/reports/student/{id}
GET /api/v1/bff/reports/classroom/{id}/grades
GET /api/v1/bff/reports/teacher/{id}/assessments
```

**Fase 3 - Búsqueda Unificada**:

```http
GET /api/v1/bff/search?q=nombre&type=student|classroom|assessment
```

**Fase 4 - Notificaciones en Tiempo Real**:

```websocket
ws://localhost:8085/api/v1/bff/notifications
```

---

## ⚙️ Configuración

### application.properties

```properties
spring.application.name=BFF_Web
server.port=8085
server.servlet.context-path=/

# URLs de los microservicios
feign.client.config.default.connect-timeout=5000
feign.client.config.default.read-timeout=10000

# Circuit Breaker para cada servicio
resilience4j.circuitbreaker.instances.student-service.slidingWindowSize=10
resilience4j.circuitbreaker.instances.student-service.failureRateThreshold=50

resilience4j.circuitbreaker.instances.classroom-service.slidingWindowSize=10
resilience4j.circuitbreaker.instances.classroom-service.failureRateThreshold=50

resilience4j.circuitbreaker.instances.assessment-service.slidingWindowSize=10
resilience4j.circuitbreaker.instances.assessment-service.failureRateThreshold=50

# Security
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://auth-server:8000

# Swagger
springdoc.swagger-ui.path=/docs
```

---

## 🔒 Seguridad

### OAuth2 (Configurado)

El JWT de OAuth2 se requiere para toda solicitud:

```bash
curl -H "Authorization: Bearer {JWT_TOKEN}" \
  http://localhost:8085/api/v1/bff/dashboard/student/1
```

### Circuit Breaker

Si un microservicio falla:

```
Intento 1: Normal
  ↓ (falla)
Intento 2-3: Retry automático
  ↓ (siguen fallando)
Circuit Breaker SE ABRE
  ↓
Retorna: Gateway error o caché
```

---

## 📊 Caching (Planificado)

```java
@Cacheable(value = "student-profiles", key = "#studentId")
public StudentProfileResponseDTO getStudentProfile(Long studentId) {
    // Caché por 5 minutos
}

@CachePut(value = "student-profiles", key = "#studentId")
public StudentProfileResponseDTO updateStudentProfile(Long studentId, ...) {
    // Invalida caché después de actualizar
}

@CacheEvict(value = "student-profiles", allEntries = true)
public void clearCache() {
    // Limpia todo el caché
}
```

---

## 🧪 Testing (Próximo)

```bash
mvn test
```

---

## 🤝 Roadmap de Desarrollo

### Fase 1 (Actual)
- [ ] Implementar StudentClient
- [ ] Implementar ClassroomClient
- [ ] Implementar AssessmentClient
- [ ] Implementar DashboardService
- [ ] GET `/api/v1/bff/dashboard/student/{id}`

### Fase 2
- [ ] GET `/api/v1/bff/dashboard/teacher/{id}`
- [ ] GET `/api/v1/bff/dashboard/admin`
- [ ] Sistema de reportes
- [ ] Exportación a PDF/Excel

### Fase 3
- [ ] Búsqueda unificada
- [ ] Caché distribuido (Redis)
- [ ] WebSocket para notificaciones

### Fase 4
- [ ] Analytics
- [ ] Predicciones (ML)
- [ ] Integración con sistemas externos

---

## 📝 Ejemplo de Respuesta Esperada

```json
{
  "studentInfo": {
    "id": 1,
    "fullName": "Juan García",
    "rut": "15.123.456-7",
    "allergies": ["Maní"],
    "classroom": "4to A"
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
      "subject": "Matemáticas",
      "daysLeft": 5
    }
  ],
  "recentGrades": [
    {
      "assessment": "Tarea 1",
      "score": 6.5,
      "subject": "Matemáticas",
      "date": "2024-06-10",
      "feedback": "Excelente"
    }
  ],
  "performanceMetrics": {
    "average": 6.3,
    "passRate": "100%",
    "trend": "IMPROVING",
    "lastUpdated": "2024-06-16T14:00:00"
  }
}
```

---

## 🐛 Troubleshooting

### Error: "No se puede conectar a Student Manager"

**Causa**: Student Manager no está corriendo en puerto 8081  
**Solución**: Iniciar Student Manager

```bash
cd backend/Student_Manager
mvn spring-boot:run
```

### Error: "CircuitBreaker abierto para X service"

**Causa**: Demasiados fallos en ese servicio  
**Solución**: Esperar a que se recupere (waitDurationInOpenState)

### Error: "Authorization required"

**Causa**: Falta token JWT  
**Solución**: Incluir header `Authorization: Bearer {token}`

---

## 📞 Soporte

Para problemas durante el desarrollo:
- Abre una issue
- Contacta al equipo de backend

---

## 📋 Notas Importantes

- ⚠️ Microservicio en desarrollo - funcionalidades limitadas
- ⚠️ No tiene base de datos propia
- ✅ Usa OpenFeign para comunicación
- ✅ Circuit Breaker habilitado para todos los servicios
- ✅ OAuth2 configurado pero no completamente habilitado

---

**Última actualización**: 16 de Mayo de 2026

**Próxima Sesión de Desarrollo**: Implementar clientes Feign y primer endpoint de dashboard

