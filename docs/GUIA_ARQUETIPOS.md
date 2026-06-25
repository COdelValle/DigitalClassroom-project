# Guía de Arquetipos - Generar Nuevos Proyectos

**Versión**: 1.0.0  
**Fecha**: 19 de mayo de 2026  
**Propósito**: Cómo usar los arquetipos de DigitalClassroom para crear nuevos microservicios/módulos

---

## 📋 ¿Qué es un Arquetipo?

Un **arquetipo** es una plantilla reutilizable que define la estructura, configuración y código base para crear nuevos proyectos consistentes con el patrón establecido.

**Beneficios**:
- ✅ Consistencia entre proyectos
- ✅ Reduce tiempo de setup
- ✅ Buenas prácticas incorporadas
- ✅ Fácil de escalar

---

## 🏗️ Arquetipos Disponibles en DigitalClassroom

### 1. Arquetipo: Microservicio CRUD

**Uso**: Crear nuevo microservicio que gestione entidades (similar a Student Manager, Classroom Manager, Assessment Manager)

**Ubicación**: `backend/` (cualquier subcarpeta)

**Estructura Base**:

```
NuevoMicroservicio/
├── src/
│   ├── main/
│   │   ├── java/com/classroom/
│   │   │   ├── controller/
│   │   │   │   └── EntityController.java
│   │   │   ├── service/
│   │   │   │   ├── EntityService.java
│   │   │   │   └── impl/EntityServiceImpl.java
│   │   │   ├── repository/
│   │   │   │   └── EntityRepository.java
│   │   │   ├── entity/
│   │   │   │   └── Entity.java
│   │   │   ├── dto/
│   │   │   │   ├── EntityDTO.java
│   │   │   │   └── EntityResponseDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── EntityNotFoundException.java
│   │   │   │   ├── EntityValidationException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── FeignConfig.java
│   │   │   └── NuevoMicroservicioApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
│       └── java/com/classroom/
│           ├── controller/
│           │   └── EntityControllerTest.java
│           ├── service/
│           │   └── EntityServiceTest.java
│           └── repository/
│               └── EntityRepositoryTest.java
├── pom.xml
├── Dockerfile
├── README.md
└── mvnw
```

---

## 🚀 Paso a Paso: Crear Nuevo Microservicio

### Paso 1: Copiar Estructura Base

```bash
# Opción 1: Usar un existente como referencia
cp -r backend/Student_Manager backend/NuevoMicroservicio
cd backend/NuevoMicroservicio

# Opción 2: Crear desde cero
mkdir -p backend/NuevoMicroservicio/{src/main/java/com/classroom/{controller,service,repository,entity,dto,exception,config},src/main/resources,src/test/java/com/classroom}
```

### Paso 2: Actualizar pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.classroom</groupId>
    <artifactId>nuevo-microservicio</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Nuevo Microservicio</name>
    <description>Descripción del nuevo microservicio</description>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.6</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Feign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        
        <!-- Resilience4j -->
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot3</artifactId>
        </dependency>
        
        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- OpenAPI/Swagger -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.1.0</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Paso 3: Crear Clase Principal

**NuevoMicroservicioApplication.java**:

```java
package com.classroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NuevoMicroservicioApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NuevoMicroservicioApplication.class, args);
    }
}
```

### Paso 4: Crear Entidad JPA

**Entity.java**:

```java
package com.classroom.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "entities")
public class Entity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es requerido")
    @Column(unique = true)
    private String name;
    
    @NotNull(message = "El estado es requerido")
    private String status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
}
```

### Paso 5: Crear Repository

**EntityRepository.java**:

```java
package com.classroom.repository;

import com.classroom.entity.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Long>,
                                          JpaSpecificationExecutor<Entity> {
    
    Optional<Entity> findByName(String name);
    List<Entity> findByStatus(String status);
}
```

### Paso 6: Crear DTOs

**EntityDTO.java** (Request):

```java
package com.classroom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityDTO {
    
    @NotBlank(message = "El nombre es requerido")
    private String name;
    
    private String status;
}
```

**EntityResponseDTO.java** (Response):

```java
package com.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityResponseDTO {
    
    private Long id;
    private String name;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Paso 7: Crear Service

**EntityService.java** (Interfaz):

```java
package com.classroom.service;

import com.classroom.dto.EntityDTO;
import com.classroom.dto.EntityResponseDTO;
import java.util.List;

public interface EntityService {
    
    List<EntityResponseDTO> getAll();
    EntityResponseDTO getById(Long id);
    EntityResponseDTO create(EntityDTO dto);
    EntityResponseDTO update(Long id, EntityDTO dto);
    void delete(Long id);
}
```

**EntityServiceImpl.java** (Implementación):

```java
package com.classroom.service.impl;

import com.classroom.dto.EntityDTO;
import com.classroom.dto.EntityResponseDTO;
import com.classroom.entity.Entity;
import com.classroom.exception.EntityNotFoundException;
import com.classroom.exception.EntityValidationException;
import com.classroom.repository.EntityRepository;
import com.classroom.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntityServiceImpl implements EntityService {
    
    @Autowired
    private EntityRepository repository;
    
    @Override
    public List<EntityResponseDTO> getAll() {
        return repository.findAll()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public EntityResponseDTO getById(Long id) {
        Entity entity = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
        return mapToDTO(entity);
    }
    
    @Override
    public EntityResponseDTO create(EntityDTO dto) {
        validateEntity(dto);
        
        Entity entity = new Entity();
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        
        Entity saved = repository.save(entity);
        return mapToDTO(saved);
    }
    
    @Override
    public EntityResponseDTO update(Long id, EntityDTO dto) {
        Entity entity = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
        
        validateEntity(dto);
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        
        Entity updated = repository.save(entity);
        return mapToDTO(updated);
    }
    
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    private void validateEntity(EntityDTO dto) {
        if (repository.findByName(dto.getName()).isPresent()) {
            throw new EntityValidationException("Name already exists: " + dto.getName());
        }
    }
    
    private EntityResponseDTO mapToDTO(Entity entity) {
        return new EntityResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
```

### Paso 8: Crear Controller

**EntityController.java**:

```java
package com.classroom.controller;

import com.classroom.dto.EntityDTO;
import com.classroom.dto.EntityResponseDTO;
import com.classroom.service.EntityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/entities")
@Validated
public class EntityController {
    
    @Autowired
    private EntityService service;
    
    @GetMapping
    public ResponseEntity<List<EntityResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<EntityResponseDTO> create(@Valid @RequestBody EntityDTO dto) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.create(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityResponseDTO> update(
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

### Paso 9: Crear Excepciones

**EntityNotFoundException.java**:

```java
package com.classroom.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
```

**EntityValidationException.java**:

```java
package com.classroom.exception;

public class EntityValidationException extends RuntimeException {
    public EntityValidationException(String message) {
        super(message);
    }
}
```

**GlobalExceptionHandler.java**:

```java
package com.classroom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "NOT_FOUND");
        error.put("message", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
    }
    
    @ExceptionHandler(EntityValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(EntityValidationException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "VALIDATION_ERROR");
        error.put("message", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }
}
```

### Paso 10: Configurar application.yml

**src/main/resources/application.yml**:

```yaml
spring:
  application:
    name: nuevo-microservicio
  datasource:
    url: jdbc:mariadb://localhost:3306/nuevo_db
    username: nuevo_user
    password: nuevo_password
    driver-class-name: org.mariadb.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true

server:
  port: 8084  # Ajustar puerto
  servlet:
    context-path: /api/v1

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000

logging:
  level:
    root: INFO
    com.classroom: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
```

### Paso 11: Crear Tests

**EntityServiceTest.java**:

```java
package com.classroom.service;

import com.classroom.dto.EntityDTO;
import com.classroom.entity.Entity;
import com.classroom.exception.EntityNotFoundException;
import com.classroom.repository.EntityRepository;
import com.classroom.service.impl.EntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntityServiceTest {
    
    @Mock
    private EntityRepository repository;
    
    @InjectMocks
    private EntityServiceImpl service;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testGetByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> {
            service.getById(1L);
        });
    }
}
```

### Paso 12: Actualizar docker-compose.yml (si aplica)

En la raíz del proyecto:

```yaml
version: '3.8'

services:
  nuevo-microservicio:
    build: ./backend/NuevoMicroservicio
    ports:
      - "8084:8084"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://mariadb:3306/nuevo_db
      SPRING_DATASOURCE_USERNAME: nuevo_user
      SPRING_DATASOURCE_PASSWORD: nuevo_password
    depends_on:
      - mariadb
```

---

## ✅ Checklist Final

```
□ pom.xml actualizado
□ Clase Application.java creada
□ Entity creada con anotaciones
□ DTO Request/Response creados
□ Repository creado
□ Service interfaz + implementación
□ Controller creado
□ Excepciones creadas
□ GlobalExceptionHandler creado
□ application.yml configurado
□ Tests unitarios creados
□ Base de datos creada
□ Docker image configurada
□ README.md escrito
□ Puerto único (no colisiona con otros)
□ mvn clean install ejecutado exitosamente
```

---

## 🧪 Probar Nuevo Microservicio

```bash
cd backend/NuevoMicroservicio

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run

# Probar
curl http://localhost:8084/api/v1/entities
```

---

## 🔄 Integración con BFF

Si el nuevo microservicio debe ser accesible desde el frontend:

1. **Crear Feign Client**:

```java
@FeignClient(
    name = "nuevo-service",
    url = "${microservices.nuevo.url}"
)
public interface NuevoClient {
    @GetMapping("/entities")
    List<EntityResponseDTO> getAll();
}
```

2. **Agregar a BFF** `application.yml`:

```yaml
microservices:
  nuevo:
    url: http://localhost:8084/api/v1
```

---

## 📝 Notas

- Cambiar nombres genéricos (Entity, entity) por nombres específicos (Student, student)
- Ajustar puerto según necesidad
- Configurar base de datos correcta
- Actualizar README.md con información específica

---

**Última actualización**: 19 de mayo de 2026 ✓

*Para dudas sobre arquetipos: consultar equipo de arquitectura*
