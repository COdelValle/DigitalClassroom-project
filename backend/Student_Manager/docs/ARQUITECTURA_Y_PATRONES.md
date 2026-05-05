# Arquitectura y Patrones de Diseño
## Student Manager Microservice

---

## 1. Arquitectura General

### Patrón: Layered Architecture (Arquitectura en Capas)

```
┌─────────────────────────────────────────────────────┐
│           REST API - HTTP Endpoints                 │
│              StudentController                       │
├─────────────────────────────────────────────────────┤
│             Business Logic Layer                    │
│              StudentService                         │
├─────────────────────────────────────────────────────┤
│           Data Access Layer (Repository)            │
│              StudentRepository                      │
├─────────────────────────────────────────────────────┤
│           Database - MariaDB/MySQL                  │
│              students (Tabla)                       │
└─────────────────────────────────────────────────────┘
```

**Ventajas:**
- Separación de responsabilidades
- Fácil testing unitario
- Cambios en BD sin afectar lógica
- Escalabilidad

---

## 2. Patrones de Diseño Utilizados

### A. Patrón MVC (Model-View-Controller)

**StudentController (View/Handler):**
```java
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    @PostMapping
    public ResponseEntity<StudentProfileResponseDTO> create(...) { }
}
```
- Maneja peticiones HTTP
- Retorna respuestas JSON
- Delega lógica a service

**StudentService (Controller/Logic):**
```java
@Service
public class StudentService {
    public StudentProfileResponseDTO create(StudentRequestDTO req) { }
}
```
- Implementa reglas de negocio
- Valida datos
- Orquesta operaciones

**Student (Model):**
```java
@Entity
public class Student {
    private String rut;
    private String firstName;
    // ...
}
```
- Representa datos
- Mapea a tabla en BD

### B. Patrón DTO (Data Transfer Object)

**Problema:** 
Exponer Entity directamente puede causar:
- Lazy loading issues
- Exposición de datos sensibles
- Acoplamiento cliente-servidor

**Solución:**
```java
// Entity (BD)
@Entity
public class Student {
    private Long id;
    private String rut;
    private List<LegalRepresentativeDTO> legalRepresentatives;
}

// DTO - Vista Profesor (segura)
public class StudentProfileResponseDTO {
    private Long id;
    private String rut;
    private List<EmergencyContactDTO> emergencyContacts;
    // Sin email, teléfono directo de representantes
}

// DTO - Vista Admin (completa)
public class StudentFullResponseDTO {
    private Long id;
    private String rut;
    private List<LegalRepresentativeDTO> legalRepresentatives;
    // Toda la información
}
```

**Ventajas:**
- Múltiples vistas del mismo dato
- No expone campos sensibles
- Transporte de datos ligero

### C. Patrón Mapper (MapStruct)

**Sin Mapper (hardcoded):**
```java
private StudentProfileResponseDTO mapEntityToProfileResponse(Student entity) {
    return StudentProfileResponseDTO.builder()
            .id(entity.getId())
            .rut(entity.getRut())
            .fullName(entity.getFirstName() + " " + entity.getLastName())
            .emergencyContacts(entity.getLegalRepresentatives().stream()
                    .map(lr -> StudentProfileResponseDTO.EmergencyContactDTO.builder()
                            .name(lr.getFullName())
                            .phoneNumbers(lr.getPhoneNumber())
                            .relationship(lr.getRelationship())
                            .build())
                    .toList())
            .build();
}
```
**Problemas:**
- Código repetitivo
- Propenso a errores
- Difícil mantener
- Performance mediocre

**Con MapStruct:**
```java
@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "fullName", source = "firstName", 
             qualifiedByName = "getFullName")
    StudentProfileResponseDTO entityToProfileResponseDto(Student entity);
    
    @Named("getFullName")
    default String getFullName(Student student) {
        return student.getFirstName() + " " + student.getLastName();
    }
}
```

**Ventajas:**
- Código generado automático en compile-time
- Type-safe
- Performance óptimo (sin reflexión)
- Mantenible

### D. Patrón Repository (Data Access Object)

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByRut(String rut);
    Optional<Student> findByRut(String rut);
}
```

**Beneficios:**
- Abstrae acceso a BD
- Queries automáticas con Spring Data
- Testeable (puede hacerse mock)
- Independencia de BD

### E. Patrón Transactional

```java
@Transactional
public StudentProfileResponseDTO create(StudentRequestDTO request) {
    if (studentRepository.existsByRut(request.getRut())) {
        throw new StudentAlreadyExistsException(...);
    }
    Student student = studentMapper.requestDtoToEntity(request);
    Student saved = studentRepository.save(student);
    return studentMapper.entityToProfileResponseDto(saved);
}
```

**@Transactional comportamiento:**
1. **Inicia transacción** antes de método
2. **Si excepción**: ROLLBACK (deshace cambios)
3. **Si éxito**: COMMIT (persiste cambios)
4. **finally**: Cierra recursos

**Ventajas:**
- Consistencia de datos
- Atomicidad de operaciones
- Manejo de errores transparente

### F. Patrón Singleton (Spring Beans)

```java
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
}
```

Spring crea **una única instancia** por JVM:
- Reutilización de recursos
- Thread-safe (Spring maneja)
- Inyección de dependencias

### G. Patrón Builder

**Entity:**
```java
Student student = Student.builder()
    .rut("12.345.678-9")
    .firstName("Juan")
    .lastName("García")
    .birthDate(new Date())
    .build();
```

**Ventajas:**
- Legibilidad
- Valores por defecto
- No requiere orden de parámetros
- Facilita testing

### H. Patrón Chain of Responsibility (Exception Handling)

```
Request → Controller → GlobalExceptionHandler
                           ↓
                    ¿MethodArgumentNotValidException?
                           ↓ YES
                    handleValidationExceptions()
                           ↓ NO
                    ¿ResourceNotFoundException?
                           ↓ YES
                    handleResourceNotFound()
                           ↓ NO
                    handleGenericException()
```

Cada handler decide si procesa o pasa al siguiente.

### I. Patrón Stream & Functional Programming

```java
return studentRepository.findAll(pageable)
    .map(studentMapper::entityToShortResponseDto);
```

Vs. tradicional:
```java
List<StudentShortResponseDTO> results = new ArrayList<>();
for (Student student : studentRepository.findAll(pageable)) {
    results.add(studentMapper.entityToShortResponseDto(student));
}
return results;
```

**Ventajas:**
- Más conciso
- Lazy evaluation
- Fácil composición
- Paralelizable (si necesario)

---

## 3. Flujos de Datos

### Flujo 1: Crear Estudiante

```
CLIENT
  │
  ├─→ POST /api/v1/students
  │   Content-Type: application/json
  │   Body: StudentRequestDTO
  │
  └─→ CONTROLLER (StudentController)
      │
      ├─→ @Valid deserializa DTO
      │   ├─ RUTValidator.isValid()
      │   ├─ @NotBlank validations
      │   └─ @Size validations
      │
      ├─ Si erro → GlobalExceptionHandler
      │           → handleValidationExceptions()
      │           → 400 Bad Request
      │
      ├─→ log.info("POST /api/v1/students...")
      │
      └─→ studentService.create(request)
          │
          ├─→ @Transactional comienza
          │
          ├─→ existsByRut() query BD
          │   ├─ Si existe → StudentAlreadyExistsException
          │   │              → 409 Conflict
          │   │
          │   └─ Si no → continúa
          │
          ├─→ requestDtoToEntity() (MapStruct)
          │
          ├─→ studentRepository.save()
          │   │
          │   └─→ BD: INSERT INTO students VALUES(...)
          │
          ├─→ entityToProfileResponseDto() (MapStruct)
          │
          ├─→ log.info("Estudiante creado...")
          │
          └─→ @Transactional COMMIT
              │
              └─→ Retorna StudentProfileResponseDTO
                  │
                  └─→ JSON serializado
                      │
                      └─→ CLIENT: 201 Created
```

### Flujo 2: Listar con Paginación

```
CLIENT
  │
  ├─→ GET /api/v1/students?page=0&size=10&sort=id,desc
  │
  └─→ CONTROLLER
      │
      ├─→ Pageable pageable = PageRequest(page=0, size=10)
      │
      └─→ studentService.findAllForTable(pageable)
          │
          ├─→ @Transactional(readOnly=true)
          │   (Optimiza para solo lectura)
          │
          ├─→ studentRepository.findAll(pageable)
          │   │
          │   └─→ BD: SELECT * FROM students
          │        LIMIT 10 OFFSET 0
          │        ORDER BY id DESC
          │
          ├─→ .map(entityToShortResponseDto)
          │   (Spring Streams, lazy)
          │
          └─→ Page<StudentShortResponseDTO>
              │
              ├─ content: [{id: 1, rut: "...", fullName: "..."}, ...]
              ├─ totalElements: 150
              ├─ totalPages: 15
              ├─ currentPage: 0
              └─ size: 10
                  │
                  └─→ JSON
                      │
                      └─→ CLIENT: 200 OK
```

### Flujo 3: Manejo de Errores

```
CLIENT envía RUT duplicado

  │
  ├─→ StudentService.create() detects duplicate
  │
  ├─→ throw new StudentAlreadyExistsException(msg)
  │
  ├─→ GlobalExceptionHandler.handleStudentAlreadyExists()
  │   │
  │   ├─→ @ExceptionHandler intercepta
  │   │
  │   ├─→ Construye respuesta:
  │   │   {
  │   │     "timestamp": "2026-05-02T10:30:45",
  │   │     "message": "Ya existe estudiante con RUT...",
  │   │     "status": 409
  │   │   }
  │   │
  │   ├─→ log.warn("Intento de crear estudiante duplicado...")
  │   │
  │   └─→ ResponseEntity(response, HttpStatus.CONFLICT)
  │
  └─→ CLIENT: 409 Conflict + JSON body
```

---

## 4. Seguridad - Capas

### Layer 1: Validación de Entrada

```java
@PostMapping
public ResponseEntity<StudentProfileResponseDTO> create(
    @Valid @RequestBody StudentRequestDTO request)
```

- **@Valid**: Dispara validadores
- Rechaza datos malformados antes de lógica

### Layer 2: Lógica de Negocio

```java
if (studentRepository.existsByRut(request.getRut())) {
    throw new StudentAlreadyExistsException(...);
}
```

- Valida reglas de negocio
- Previene datos duplicados

### Layer 3: Seguridad Spring

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/swagger-ui/**").permitAll()
    .anyRequest().authenticated()
)
```

- Control de acceso por rol/endpoint
- Autenticación obligatoria

### Layer 4: CORS

```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000"
));
```

- Solo orígenes autorizados
- Previene CSRF en navegadores

### Layer 5: Base de Datos

```java
@Column(unique = true, nullable = false)
private String rut;
```

- Restricciones en BD
- Last line of defense

---

## 5. Performance - Optimizaciones

### A. Paginación

```java
@GetMapping
public Page<StudentShortResponseDTO> getAll(@PageableDefault(size = 10) Pageable pageable)
```

Sin paginación:
```sql
SELECT * FROM students;  -- 100 mil registros = timeout
```

Con paginación:
```sql
SELECT * FROM students LIMIT 10 OFFSET 0;  -- 10 registros = rápido
```

### B. Índices en BD

```java
@Table(name = "students", indexes = {
    @Index(name = "idx_rut", columnList = "rut", unique = true)
})
```

Sin índice: SELECT 100ms  
Con índice: SELECT 1ms

### C. Pool de Conexiones

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
```

Reutiliza conexiones en lugar de crear nuevas.

### D. Batch Processing

```properties
hibernate.jdbc.batch_size=20
```

20 inserts en 1 request vs 20 requests.

### E. DTO Reducidos

```java
// Para tablas, solo datos necesarios
StudentShortResponseDTO {
    Long id;
    String rut;
    String fullName;
}

// Vs ProfileDTO más pesado
StudentProfileResponseDTO {
    + List<EmergencyContactDTO> emergencyContacts;
    + List<String> allergies;
}
```

Menor transferencia de datos.

### F. Transacciones Read-Only

```java
@Transactional(readOnly = true)
public... findAllForTable(Pageable pageable)
```

BD sabe que puede optimizar (sin logs de cambios).

---

## 6. Testabilidad

### Inyección de Dependencias

```java
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
}
```

En tests:
```java
@Mock
private StudentRepository studentRepository;

@InjectMocks
private StudentService studentService;
```

Reemplaza BD real con mock.

### Interfaces en Repositorio

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>
```

En tests:
```java
when(studentRepository.existsByRut(anyString())).thenReturn(false);
```

Mock del repositorio completamente.

---

## 7. Escalabilidad Futura

### A. Implementar Caché

```java
@Cacheable(value = "students", key = "#id")
public StudentFullResponseDTO findFullDetailById(Long id)
```

Redis/Memcached para búsquedas frecuentes.

### B. Evento Driven

```java
@TransactionalEventListener
void onStudentCreated(StudentCreatedEvent event) {
    // Notificar otros servicios
    messagingService.send(event);
}
```

Comunicación asincrónica entre servicios.

### C. Circuit Breaker

```java
@CircuitBreaker(name = "studentService")
public StudentFullResponseDTO findById(Long id)
```

Resilience4j ya integrado, solo falta implementar.

### D. GraphQL

Alternativa a REST para consultas complejas:
```graphql
query {
    student(id: 1) {
        id
        fullName
        emergencyContacts {
            name
            phone
        }
    }
}
```

---

## 8. Monitoreo y Logging

### Structured Logging

```java
@Slf4j
public class StudentService {
    public void delete(Long id) {
        log.info("DELETE student id={}", id);  // Bueno
        // vs
        log.info("DELETE student id=" + id);   // Evita concatenación
    }
}
```

Facilita búsquedas en logs centralizados (ELK Stack).

### Métricas

```properties
management.endpoints.web.exposure.include=health,metrics
```

```
http://localhost:8080/actuator/metrics
→ http.server.requests
→ jvm.memory.used
→ db.connection.pool.size
```

Intégra con Prometheus/Grafana.

---

## 9. Comparación: Antes vs Después

| Aspecto | Antes | Después |
|---------|-------|---------|
| **Excepciones** | RuntimeException genérico | StudentAlreadyException específico |
| **Mapeo DTO** | Hardcoded en service | MapStruct automático |
| **Seguridad** | Ninguna | Spring Security + CORS |
| **Paginación** | Todo en memoria | Spring Data Page |
| **Auditoría** | Manual (campo createdBy) | @CreationTimestamp automático |
| **Documentación** | Comentarios mínimos | JavaDoc + Swagger completo |
| **Tests** | Ninguno | StudentServiceTest básico |
| **Logging** | System.out.println | @Slf4j estructurado |
| **Dockerfile** | Ubuntu dummy | Multi-stage optimizado |
| **Configuración** | Hardcoded | Variables de entorno |
| **Validación** | En servicio | @Valid + validadores custom |

---

## Resumen

El Student Manager implementa:
- ✅ **Layered Architecture** clara
- ✅ **Patrones SOLID** (Single Responsibility, Dependency Inversion)
- ✅ **GRASP** (General Responsibility Assignment Software Patterns)
- ✅ **Enterprise Patterns** (DTO, Mapper, Repository, Transactional)
- ✅ **Security Patterns** (validación, autenticación)
- ✅ **Scalability Patterns** (paginación, batch, índices)
- ✅ **Testing Patterns** (Injección de dependencias, mocks)

Resultado: **Aplicación robusta, mantenible y escalable**

