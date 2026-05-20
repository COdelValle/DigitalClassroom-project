# Patrones de Diseño y Arquetipos - DigitalClassroom

**Versión**: 1.0.0  
**Fecha**: 19 de mayo de 2026  
**Autores**: Equipo DigitalClassroom

---

## 📋 Introducción

Este documento explica los patrones de diseño y arquetipos de software utilizados en el proyecto DigitalClassroom. Se describe la justificación de cada elección arquitectónica y cómo impactan en la calidad, mantenibilidad y escalabilidad del sistema.

---

## 🏗️ Arquitectura General

### Arquitectura de Microservicios

**Patrón**: Microservicios con Backend For Frontend (BFF)

**Descripción**:
```
┌─────────────────────────────────────────────────┐
│              Frontend (React + Vite)            │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
        ┌────────────────┐
        │   BFF (8080)   │
        └────┬───┬───┬───┘
             │   │   │
    ┌────────┘   │   └────────┐
    │            │            │
    ▼            ▼            ▼
┌────────┐ ┌─────────┐ ┌────────────┐
│Student │ │Classroom│ │ Assessment │
│Manager │ │Manager  │ │  Manager   │
│(8081)  │ │(8082)   │ │   (8083)   │
└────────┘ └─────────┘ └────────────┘
    │         │            │
    └─────────┴────────────┘
           │
           ▼
        MariaDB
```

**Justificación**:

1. **Escalabilidad Independiente**: Cada microservicio puede escalar según su demanda
2. **Desarrollo Independiente**: Equipos pueden trabajar en paralelo
3. **Tolerancia a Fallos**: El fallo de un servicio no derriba todo el sistema
4. **Facilidad de Despliegue**: Actualizaciones sin downtime total
5. **BFF Simplifica Frontend**: Orquestación centralizada y transformación de datos

**Ventajas**:
- ✅ Separación de responsabilidades clara
- ✅ Escalabilidad horizontal
- ✅ Resiliencia mediante Circuit Breaker
- ✅ API específica para UI (BFF)

**Desventajas**:
- ⚠️ Mayor complejidad operacional
- ⚠️ Necesidad de coordinación entre servicios
- ⚠️ Latencia de red entre servicios

---

## 🎯 Arquetipos Personalizados

### Arquetipo: Microservicio CRUD con Validación

**Descripción**: Patrón base para los microservicios de gestión de datos (Student, Classroom, Assessment Manager).

**Estructura**:

```
MicroservicioManager/
├── controller/
│   └── EntityController.java
│       ├── GET /api/v1/entities
│       ├── GET /api/v1/entities/{id}
│       ├── POST /api/v1/entities
│       ├── PUT /api/v1/entities/{id}
│       └── DELETE /api/v1/entities/{id}
│
├── service/
│   ├── EntityService.java (interfaz)
│   └── impl/
│       └── EntityServiceImpl.java
│           ├── validar()
│           ├── procesar()
│           └── guardar()
│
├── repository/
│   └── EntityRepository.java
│       (Spring Data JPA)
│
├── entity/
│   └── Entity.java (JPA Entity)
│
├── dto/
│   ├── EntityDTO.java (request)
│   └── EntityResponseDTO.java (response)
│
├── exception/
│   ├── EntityNotFoundException.java
│   ├── EntityValidationException.java
│   └── GlobalExceptionHandler.java
│
└── config/
    ├── SecurityConfig.java
    └── FeignClientConfig.java
```

**Componentes Clave**:

#### 1. Controller (REST)

```java
@RestController
@RequestMapping("/api/v1/entities")
@Validated
public class EntityController {
    
    @GetMapping
    public ResponseEntity<List<EntityDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<EntityDTO> create(@Valid @RequestBody EntityDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityDTO> update(
        @PathVariable Long id, 
        @Valid @RequestBody EntityDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### 2. Service (Lógica de Negocio)

```java
@Service
public class EntityService {
    
    private final EntityRepository repository;
    
    public EntityDTO create(EntityDTO dto) {
        // Validar
        validar(dto);
        
        // Procesar
        Entity entity = mapearDtOaEntity(dto);
        
        // Guardar
        Entity guardada = repository.save(entity);
        
        return mapearEntityADtO(guardada);
    }
    
    private void validar(EntityDTO dto) {
        if (repository.existsByUniqueField(dto.getUniqueField())) {
            throw new EntityValidationException("Campo único duplicado");
        }
    }
}
```

#### 3. Repository (Acceso a Datos)

```java
@Repository
public interface EntityRepository extends JpaRepository<Entity, Long>, 
                                           JpaSpecificationExecutor<Entity> {
    
    Optional<Entity> findByUniqueField(String field);
    boolean existsByUniqueField(String field);
    List<Entity> findByStatus(String status);
}
```

#### 4. Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }
    
    @ExceptionHandler(EntityValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(EntityValidationException ex) {
        return ResponseEntity.status(400)
            .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }
}
```

**Ventajas**:

- ✅ Estructura consistente entre servicios
- ✅ Fácil de entender y mantener
- ✅ Código reutilizable
- ✅ Separación de responsabilidades clara

---

### Arquetipo: Comunicación Inter-Servicios

**Patrón**: Feign Client + Circuit Breaker

**Descripción**: Cómo los microservicios se comunican entre sí de forma resiliente.

**Ejemplo**:

```java
@FeignClient(
    name = "student-service",
    url = "${student-service.url:http://localhost:8081}",
    configuration = FeignConfig.class
)
public interface StudentClient {
    
    @GetMapping("/api/v1/students/{id}")
    StudentDTO getStudent(@PathVariable Long id);
    
    @GetMapping("/api/v1/students/{id}/exists")
    boolean exists(@PathVariable Long id);
}
```

**Con Circuit Breaker**:

```java
@Service
public class StudentValidationService {
    
    private final StudentClient studentClient;
    
    @CircuitBreaker(
        name = "student-service",
        fallbackMethod = "validateStudentFallback"
    )
    public boolean validateStudent(Long studentId) {
        return studentClient.exists(studentId);
    }
    
    public boolean validateStudentFallback(Long studentId, Exception ex) {
        // Fallback: permitir con validación posterior
        logger.warn("Student service down for ID: {}", studentId, ex);
        return true; // O false según política
    }
}
```

**Ventajas**:

- ✅ Comunicación declarativa
- ✅ Manejo automático de timeouts
- ✅ Tolerancia a fallos con Circuit Breaker
- ✅ Retry automático

---

### Arquetipo: Backend For Frontend (BFF)

**Patrón**: Agregación y Transformación de Datos

**Descripción**: El BFF simplifica el frontend orquestando múltiples llamadas a microservicios.

**Ejemplo - Endpoint Dashboard**:

```java
@RestController
@RequestMapping("/api")
public class DashboardController {
    
    private final StudentClient studentClient;
    private final ClassroomClient classroomClient;
    private final AssessmentClient assessmentClient;
    
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<DashboardDTO> getDashboard(@PathVariable Long userId) {
        
        // Llamar a múltiples servicios
        UserDTO user = getUser(userId);
        List<ClassDTO> classes = classroomClient.getClassesByTeacher(userId);
        List<StudentDTO> students = studentClient.getStudentsInClasses(
            classes.stream().map(ClassDTO::getId).collect(toList())
        );
        
        // Transformar y agregar datos
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setUser(user);
        dashboard.setClasses(classes);
        dashboard.setStudentCount(students.size());
        
        return ResponseEntity.ok(dashboard);
    }
}
```

**Ventajas**:

- ✅ Frontend no necesita saber de múltiples servicios
- ✅ Lógica de agregación centralizada
- ✅ Mejor rendimiento con caching
- ✅ API específica para UI

---

## 🔐 Patrones de Seguridad

### Patrón: JWT (JSON Web Tokens)

**Descripción**: Autenticación sin estado basada en tokens.

**Flujo**:

```
1. Usuario envía credenciales
   POST /api/auth/login { username, password }

2. BFF valida y genera JWT
   ├─ Header: { alg: HS256 }
   ├─ Payload: { userId, role, exp: timestamp }
   └─ Signature: HMAC(header.payload, secret)

3. Frontend almacena token
   localStorage.setItem('accessToken', token)

4. Peticiones posteriores
   GET /api/data
   Authorization: Bearer <token>

5. BFF valida token
   ├─ Verifica firma
   ├─ Verifica expiración
   └─ Extrae claims
```

**Implementación**:

```java
@Component
public class JwtTokenProvider {
    
    public String generateToken(Long userId, String role) {
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
    
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
        );
    }
}
```

---

## 📊 Patrones de Datos

### Patrón: DTO (Data Transfer Object)

**Descripción**: Separar modelos internos de la API pública.

**Ejemplo**:

```java
// Entity (interno)
@Entity
public class Student {
    private Long id;
    private String rut;
    private String firstName;
    private String lastName;
    private String email;
    private byte[] photo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// DTO (API)
public class StudentDTO {
    private Long id;
    private String rut;
    private String firstName;
    private String lastName;
    private String email;
    // No incluye: photo, createdAt, updatedAt
}
```

**Ventajas**:

- ✅ Separa modelo interno de API pública
- ✅ Seguridad: no expone campos sensibles
- ✅ Flexibilidad: API puede cambiar sin afectar frontend
- ✅ Validación específica

---

### Patrón: Specifications (JPA)

**Descripción**: Búsquedas dinámicas sin queries hardcodeadas.

**Ejemplo**:

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>,
                                           JpaSpecificationExecutor<Student> {
}

public class StudentSpecifications {
    
    public static Specification<Student> hasFirstName(String firstName) {
        return (root, query, cb) -> 
            cb.equal(root.get("firstName"), firstName);
    }
    
    public static Specification<Student> hasRut(String rut) {
        return (root, query, cb) -> 
            cb.equal(root.get("rut"), rut);
    }
}

// Uso
List<Student> students = repository.findAll(
    where(StudentSpecifications.hasFirstName("Juan"))
    .and(StudentSpecifications.hasRut("15.123.456-7"))
);
```

**Ventajas**:

- ✅ Búsquedas dinámicas
- ✅ Reutilizable
- ✅ Type-safe
- ✅ No necesita SQL

---

## 🚀 Patrones de Rendimiento

### Circuit Breaker (Resilience4j)

**Objetivo**: Evitar cascada de fallos

**Estados**:

```
CLOSED (normal)
  ├─ Petición exitosa → sigue CLOSED
  └─ N fallos → pasa a OPEN

OPEN (protegiendo)
  └─ Después de X segundos → HALF_OPEN

HALF_OPEN (testeo)
  ├─ Petición exitosa → CLOSED
  └─ Petición fallida → OPEN
```

**Configuración**:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      student-service:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 5s
```

---

## 🔄 Patrones de Despliegue

### Patrón: Despliegue Blue-Green

**Descripción**: Dos entornos idénticos, intercambio rápido sin downtime.

```
Blue (actual)
  → 100% tráfico
  
Green (nueva versión)
  → Test

Una vez validado:
Blue ← 0% tráfico
Green ← 100% tráfico
```

---

## 🎓 Conclusión

Los patrones y arquetipos utilizados en DigitalClassroom buscan equilibrio entre:

- 🎯 **Simplicidad**: Código fácil de entender
- 🔒 **Robustez**: Tolerancia a fallos
- 📈 **Escalabilidad**: Crecimiento sin límites
- 🏃 **Velocidad**: Desarrollo rápido

Estos patrones evolucionarán con el proyecto según necesidades.

---

**Última actualización**: 19 de mayo de 2026 ✓
