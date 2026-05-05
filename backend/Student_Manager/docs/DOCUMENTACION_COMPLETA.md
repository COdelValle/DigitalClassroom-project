# Documentación Completa - Student Manager Microservice
## Explicación Línea por Línea

**Autor:** GitHub Copilot  
**Fecha:** Mayo 2, 2026  
**Versión:** 1.0  
**Ámbito:** Microservicio Student Manager - Digital Classroom Project

---

## Tabla de Contenidos

1. [Estructura General del Proyecto](#estructura-general)
2. [Configuración Maven (pom.xml)](#configuración-maven)
3. [Configuración de la Aplicación](#configuración-aplicación)
4. [Configuración de Seguridad](#configuración-seguridad)
5. [Configuración de CORS](#configuración-cors)
6. [Entidad Student](#entidad-student)
7. [MapStruct Mapper](#mapstruct-mapper)
8. [StudentService](#student-service)
9. [StudentController](#student-controller)
10. [Excepciones Personalizadas](#excepciones)
11. [GlobalExceptionHandler](#manejador-excepciones)
12. [DTOs](#data-transfer-objects)
13. [Validadores Customizados](#validadores)
14. [Tests Unitarios](#tests-unitarios)
15. [Dockerfile](#dockerfile)
16. [Application Properties](#application-properties)

---

## Estructura General del Proyecto {#estructura-general}

```
Student_Manager/
├── src/
│   ├── main/
│   │   ├── java/cl/digitalclassroom/studentmanager/
│   │   │   ├── StudentManagerApplication.java
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── mapper/
│   │   │   ├── model/
│   │   │   ├── exception/
│   │   │   └── validation/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/...
├── pom.xml
├── Dockerfile
└── README.md
```

La estructura sigue el patrón **arquitectura en capas** (layered architecture):
- **Controller**: Recibe peticiones HTTP
- **Service**: Lógica de negocio
- **Repository**: Acceso a datos
- **Model**: Entidades y DTOs
- **Config**: Configuraciones

---

## Configuración Maven (pom.xml) {#configuración-maven}

### Estructura del archivo

```xml
<?xml version="1.0" encoding="UTF-8"?>
```
**Línea 1:** Declaración XML estándar indicando que es un documento XML versión 1.0 con encoding UTF-8.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
```
**Líneas 2-5:** Declara el elemento raíz `<project>` con:
- **xmlns**: Define el namespace estándar de Maven
- **xmlns:xsi**: Namespace para validación de esquema XML
- **xsi:schemaLocation**: Especifica dónde validar contra el esquema Maven

```xml
<modelVersion>4.0.0</modelVersion>
```
**Línea 6:** Versión del modelo de objeto POM. 4.0.0 es la versión estándar moderna.

```xml
<parent>
    <groupId>cl.digital-classroom</groupId>
    <artifactId>parent</artifactId>
    <version>1.0.0</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```
**Líneas 7-10:** Define el POM padre:
- **groupId**: Identificador único del grupo
- **artifactId**: Nombre del proyecto padre
- **version**: Versión actual
- **relativePath**: Ruta relativa al pom.xml padre que hereda dependencias y configuración

```xml
<artifactId>student-manager</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>Student_Manager</name>
```
**Líneas 11-13:**
- **artifactId**: Identificador único de este módulo
- **version**: Versión SNAPSHOT (desarrollo, no estable)
- **name**: Nombre legible del proyecto

### Sección de Dependencias

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
```
**Líneas 15-20:** Spring Boot Web Starter
- Proporciona Tomcat embebido
- Incluye Spring MVC para crear APIs REST
- Sin especificar versión (hereda del padre)

```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
```
**Líneas 21-24:** Spring Data JPA
- Acceso a base de datos con Hibernate
- Métodos CRUD automáticos en repositorios

```xml
    <dependency>
        <groupId>org.mariadb.jdbc</groupId>
        <artifactId>mariadb-java-client</artifactId>
        <scope>runtime</scope>
    </dependency>
```
**Líneas 25-30:** Driver MariaDB
- **scope: runtime**: Solo se usa ejecutando, no en compilación
- Permite conexión JDBC a MariaDB

```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
    </dependency>
```
**Líneas 31-35:** Resilience4j Circuit Breaker
- Implementa patrón de resiliencia
- Maneja timeouts y fallos en llamadas externas

```xml
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${org.mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${org.mapstruct.version}</version>
        <scope>provided</scope>
    </dependency>
```
**Líneas 36-45:** MapStruct para mapeo automático DTO-Entity
- **mapstruct**: Librería de mapeo
- **mapstruct-processor**: Generador de código (scoped provided)

```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
```
**Líneas 46-49:** Spring Security
- Autenticación y autorización
- Control de acceso a endpoints

```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
```
**Líneas 50-54:** Spring Boot Test
- JUnit 5, Mockito
- scope: test (solo para testing)

### Plugin de Maven

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${org.mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```
**Líneas 72-89:** Configuración de plugins
- **maven-compiler-plugin**: Procesa anotaciones de MapStruct durante compilación
- **annotationProcessorPaths**: Define qué procesadores usar para generar código

---

## Configuración de la Aplicación {#configuración-aplicación}

### application.properties

```properties
spring.application.name=Student_Manager
```
**Línea 1:** Define el nombre de la aplicación que aparece en logs y métricas.

```properties
server.port=${SERVER_PORT:8080}
server.servlet.context-path=${CONTEXT_PATH:/}
```
**Líneas 2-3:** 
- **server.port**: Puerto del servidor (8080 por defecto)
- **SERVER_PORT**: Variable de entorno que puede sobrescribir el valor
- **:8080**: Valor por defecto si la variable no existe
- **context-path**: Ruta base de la aplicación (/ por defecto)

```properties
spring.datasource.url=${DB_URL:jdbc:mariadb://localhost:3306/student_manager_db?createDatabaseIfNotExist=true}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
```
**Líneas 5-8:** Configuración de conexión a base de datos
- **url**: Conexión JDBC (crea BD si no existe)
- **username/password**: Credenciales (IMPORTANTE: usar variables de entorno)
- **driver-class-name**: Driver JDBC a usar

```properties
spring.datasource.hikari.maximum-pool-size=${DB_POOL_SIZE:10}
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
```
**Líneas 9-11:** Configuración de HikariCP (pool de conexiones)
- **maximum-pool-size**: Máximo de conexiones simultaneas (10)
- **minimum-idle**: Conexiones mínimas inactivas (2)
- **connection-timeout**: Timeout para obtener conexión (20 segundos)

```properties
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}
```
**Línea 13:** Estrategia de creación/actualización de esquema
- **update**: Altera tablas existentes (desarrollo)
- **validate**: Solo valida (producción)
- **create-drop**: Recrear cada inicio (testing)

```properties
spring.jpa.show-sql=${SHOW_SQL:false}
spring.jpa.properties.hibernate.format_sql=true
```
**Líneas 14-15:**
- **show-sql**: Loguea queries SQL (false en producción)
- **format_sql**: Formatea SQL legible

```properties
logging.level.root=${LOG_LEVEL:INFO}
logging.level.cl.digitalclassroom.studentmanager=DEBUG
```
**Líneas 19-20:** Configuración de logs
- **root**: Nivel global (INFO por defecto)
- **studentmanager**: Nivel DEBUG para este paquete

```properties
springdoc.swagger-ui.path=/docs
management.endpoints.web.exposure.include=health,metrics,info
```
**Líneas 25-26:** Swagger y Actuator
- **swagger-ui.path**: Ruta para acceder a Swagger UI
- **exposure.include**: Endpoints expuestos para monitoreo

---

## Configuración de Seguridad {#configuración-seguridad}

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
```
**Líneas 1-3:**
- **@Configuration**: Spring carga esta clase como configuración
- **@EnableWebSecurity**: Habilita Spring Security para toda la aplicación

```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
```
**Línea 4-6:**
- **@Bean**: Registra método como bean de Spring
- **filterChain**: Retorna cadena de filtros de seguridad
- **.csrf.disable()**: Deshabilita CSRF (REST API, no cookies)

```java
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```
**Línea 7-8:**
- **STATELESS**: API REST sin sesiones (cada request es independiente)
- Ideal para APIs consumidas por clientes desacoplados

```java
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", 
                                "/v3/api-docs/**", "/docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
```
**Líneas 9-14:**
- **requestMatchers**: Define patrones de acceso
- **permitAll()**: Permite sin autenticación (Swagger, actuator)
- **authenticated()**: Requiere autenticación (resto de requests)

```java
            .httpBasic(basic -> {});

        return http.build();
```
**Líneas 15-17:**
- **httpBasic()**: Habilita autenticación HTTP Basic
- **build()**: Construye y retorna la cadena de seguridad

```java
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
```
**Líneas 18-21:**
- **PasswordEncoder Bean**: Proporciona encoder de contraseñas
- **BCryptPasswordEncoder**: Algoritmo seguro para hash de contraseñas

---

## Configuración de CORS {#configuración-cors}

### CorsConfig.java

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
```
**Líneas 1-6:** Crea bean que configura CORS
- **CorsConfiguration**: Objeto que almacena políticas CORS

```java
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:8080"
        ));
```
**Líneas 7-11:** Define orígenes permitidos
- **localhost:3000**: Frontend React típico
- **localhost:5173**: Frontend Vite en desarrollo
- **localhost:8080**: Otros servicios locales

```java
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
```
**Líneas 12-14:** Métodos HTTP permitidos
- Permite operaciones CRUD estándar

```java
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(
            "Content-Type", "Authorization"
        ));
```
**Líneas 15-18:**
- **allowedHeaders**: Headers que Frontend puede enviar (*)
- **exposedHeaders**: Headers que Frontend puede leer en respuesta

```java
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
```
**Líneas 19-20:**
- **Credentials**: Permite cookies/tokens en requests
- **MaxAge**: Cache preflight requests por 1 hora (3600 seg)

```java
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
```
**Líneas 21-23:**
- Crea fuente CORS basada en URL
- Aplica configuración a todos los paths (**)

---

## Entidad Student {#entidad-student}

### Student.java

```java
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students", indexes = {
    @Index(name = "idx_rut", columnList = "rut", unique = true),
    @Index(name = "idx_birth_date", columnList = "birth_date")
})
public class Student {
```
**Líneas 1-10:** Anotaciones de la clase
- **@Data**: Genera getters, setters, equals, hashCode (Lombok)
- **@Entity**: Indica que es entidad JPA para mapear a tabla BD
- **@Builder**: Genera patrón Builder para construcción
- **@AllArgsConstructor/@NoArgsConstructor**: Constructores (Lombok)
- **@Table**: Especifica nombre de tabla y índices
  - **idx_rut**: Índice único rápido en búsquedas por RUT
  - **idx_birth_date**: Índice para filtrar por fecha nacimiento

```java
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
```
**Líneas 11-13:** Identificador primario
- **@Id**: Clave primaria
- **GenerationType.IDENTITY**: Auto-incremento en BD

```java
    @NotBlank(message = "Se requiere ingresar RUT")
    @RUT(message = "RUT invalido")
    @Column(unique = true, nullable = false, length = 12)
    private String rut;
```
**Líneas 14-17:** Campo RUT
- **@NotBlank**: Validación (no puede estar vacío)
- **@RUT**: Validación customizada (algoritmo chileno)
- **unique = true**: Restricción BD (sin duplicados)
- **nullable = false**: Obligatorio
- **length = 12**: Máximo 12 caracteres

```java
    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(min = 2, max = 50, message = "...")
    @Column(nullable = false, length = 50)
    private String firstName;
```
**Líneas 18-21:** Primer nombre
- **@Size**: Validación de longitud
- **message**: Mensaje de error personalizado

```java
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
```
**Líneas 30-36:** Auditoría automática (Hibernate)
- **@CreationTimestamp**: Se asigna automáticamente al crear
- **updatable = false**: No se puede modificar
- **@UpdateTimestamp**: Se actualiza automáticamente

---

## MapStruct Mapper {#mapstruct-mapper}

### StudentMapper.java

```java
@Mapper(componentModel = "spring")
public interface StudentMapper {
```
**Línea 1-2:** 
- **@Mapper**: Indica a MapStruct que genere implementación
- **componentModel = "spring"**: Genera Bean de Spring

```java
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student requestDtoToEntity(StudentRequestDTO dto);
```
**Líneas 3-6:** Mapea DTO de entrada a Entidad
- **@Mapping**: Especifica mapeo de campo particular
- **ignore = true**: No mapea estos campos (BD los asigna)
- MapStruct genera código que copia otros campos automáticamente

```java
    @Mapping(target = "fullName", source = "firstName", 
             qualifiedByName = "getFullName")
    @Mapping(target = "emergencyContacts", source = "legalRepresentatives", 
             qualifiedByName = "toEmergencyContactDtos")
    StudentProfileResponseDTO entityToProfileResponseDto(Student entity);
```
**Líneas 7-11:** Mapea Entidad a DTO de Perfil
- **source**: Campo fuente
- **qualifiedByName**: Llama método custom para transformación
- Combina firstName + lastName en fullName

```java
    @Named("getFullName")
    default String getFullName(Student student) {
        if (student.getFirstName() == null || 
            student.getLastName() == null) {
            return "";
        }
        return student.getFirstName() + " " + student.getLastName();
    }
```
**Líneas 12-19:** Método custom para nombre completo
- **@Named**: Identificador para referencias en @Mapping
- **default**: Implementación directa en interfaz
- Valida nulidad antes de concatenar

```java
    @Named("toEmergencyContactDtos")
    default List<StudentProfileResponseDTO.EmergencyContactDTO> 
        toEmergencyContactDtos(List<LegalRepresentativeDTO> legalReps) {
        if (legalReps == null) {
            return List.of();
        }
        return legalReps.stream()
                .map(rep -> StudentProfileResponseDTO
                        .EmergencyContactDTO.builder()
                        .name(rep.getFullName())
                        .phoneNumbers(rep.getPhoneNumber())
                        .relationship(rep.getRelationship())
                        .build())
                .collect(Collectors.toList());
    }
```
**Líneas 20-34:** Convierte representantes a contactos emergencia
- **stream()**: Transforma lista funcional
- **map()**: Proyecta cada representante a EmergencyContact
- **builder()**: Patrón Builder para crear DTO
- **collect()**: Convierte stream a lista

---

## StudentService {#student-service}

### StudentService.java

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
```
**Líneas 1-6:**
- **@Slf4j**: Genera logger automático (Lombok)
- **@Service**: Marca como servicio (componente manejado por Spring)
- **@RequiredArgsConstructor**: Genera constructor con atributos final
- Inyección de dependencias automática

#### Método: create()

```java
    @Transactional
    public StudentProfileResponseDTO create(StudentRequestDTO request) {
        if (studentRepository.existsByRut(request.getRut())) {
            log.warn("Intento de crear estudiante con RUT duplicado: {}", 
                     request.getRut());
            throw new StudentAlreadyExistsException(
                "Ya existe un estudiante con el RUT: " + request.getRut()
            );
        }
```
**Líneas 1-9:**
- **@Transactional**: Abre transacción BD automática
- **existsByRut()**: Query custom del repository
- **log.warn()**: Loguea advertencia con el RUT
- **throw**: Lanza excepción si existe (Http 409)

```java
        Student student = studentMapper.requestDtoToEntity(request);
        Student saved = studentRepository.save(student);
        log.info("Estudiante creado exitosamente con ID: {} y RUT: {}", 
                 saved.getId(), saved.getRut());
        return studentMapper.entityToProfileResponseDto(saved);
```
**Líneas 10-14:**
- **requestDtoToEntity()**: MapStruct convierte DTO → Entity
- **save()**: Persiste en BD
- **log.info()**: Loguea éxito con ID y RUT
- **entityToProfileResponseDto()**: Convierte Entity → DTO respuesta

#### Método: findAllForTable()

```java
    @Transactional(readOnly = true)
    public Page<StudentShortResponseDTO> findAllForTable(Pageable pageable) {
        log.debug("Obteniendo estudiantes con paginación: {}", pageable);
        return studentRepository.findAll(pageable)
                .map(studentMapper::entityToShortResponseDto);
    }
```
**Líneas 1-6:**
- **readOnly = true**: Optimiza transacción (solo lectura)
- **Page<T>**: Resultado paginado (Spring Data)
- **findAll(pageable)**: Query con paginación BD
- **.map()**: Convierte cada Student a DTO usando mapper

#### Método: findProfileById()

```java
    @Transactional(readOnly = true)
    public StudentProfileResponseDTO findProfileById(Long id) {
        log.debug("Buscando perfil del estudiante con ID: {}", id);
        return studentRepository.findById(id)
                .map(student -> {
                    log.debug("Perfil encontrado para estudiante ID: {}", id);
                    return studentMapper.entityToProfileResponseDto(student);
                })
                .orElseThrow(() -> {
                    String mensaje = "Estudiante no encontrado con ID: " + id;
                    log.warn(mensaje);
                    return new ResourceNotFoundException(mensaje);
                });
    }
```
**Líneas 1-13:**
- **findById(id)**: Retorna Optional<Student>
- **.map()**: Si existe, aplica transformación
- **.orElseThrow()**: Si no existe, lanza excepción
- **log.warn()**: Loguea búsqueda fallida

#### Método: update()

```java
    @Transactional
    public StudentFullResponseDTO update(Long id, StudentRequestDTO request) {
        log.info("Iniciando actualización del estudiante con ID: {}", id);
        
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> {
                    String mensaje = "No se puede actualizar, ID no existe: " + id;
                    log.warn(mensaje);
                    return new ResourceNotFoundException(mensaje);
                });

        if (!existingStudent.getRut().equals(request.getRut()) && 
            studentRepository.existsByRut(request.getRut())) {
            log.warn("Intento de actualizar estudiante con RUT duplicado: {}", 
                     request.getRut());
            throw new StudentAlreadyExistsException(
                "Ya existe otro estudiante con el RUT: " + request.getRut()
            );
        }
```
**Línea 1-18:**
- **Verifica existencia**: Si no existe, lanza 404
- **Valida RUT**: Si cambió y ya existe otro, lanza 409
- **!equals()**: Compara RUT actual con nuevo

```java
        Student updatedStudent = Student.builder()
                .id(existingStudent.getId())
                .rut(request.getRut())
                .firstName(request.getFirstName())
                .createdAt(existingStudent.getCreatedAt())
                .build();

        Student saved = studentRepository.save(updatedStudent);
        log.info("Estudiante con ID: {} actualizado exitosamente", id);
        return studentMapper.entityToFullResponseDto(saved);
```
**Línea 19-28:**
- **Builder**: Reconstructe entidad con nuevos datos
- **preserva createdAt**: No se modifica auditoría de creación
- **save()**: Persiste cambios (updatedAt se actualiza automático)

#### Método: delete()

```java
    @Transactional
    public void delete(Long id) {
        log.info("Iniciando eliminación del estudiante con ID: {}", id);
        
        if (!studentRepository.existsById(id)) {
            String mensaje = "No se puede borrar, ID no existe: " + id;
            log.warn(mensaje);
            throw new ResourceNotFoundException(mensaje);
        }
        
        studentRepository.deleteById(id);
        log.info("Estudiante con ID: {} eliminado exitosamente", id);
    }
```
**Línea 1-12:**
- **existsById()**: Verifica existencia
- **deleteById()**: Elimina de BD
- **log.info()**: Confirma eliminación

#### Método: countTotal()

```java
    @Transactional(readOnly = true)
    public long countTotal() {
        long count = studentRepository.count();
        log.debug("Total de estudiantes en el sistema: {}", count);
        return count;
    }
```
**Línea 1-5:**
- **count()**: Query aggregate de BD
- Retorna cantidad total de estudiantes

---

## StudentController {#student-controller}

### StudentController.java

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", 
     description = "Operaciones CRUD para la gestión de alumnos")
public class StudentController {

    private final StudentService studentService;
```
**Líneas 1-9:**
- **@RestController**: Controlador REST (retorna JSON automático)
- **@RequestMapping**: Ruta base para todos los endpoints
- **@Tag**: Anotación Swagger para documentar grupo
- Inyección automática de StudentService

#### Endpoint: POST /api/v1/students

```java
    @Operation(summary = "Crear nuevo estudiante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente",
            content = @Content(mediaType = "application/json", 
                              schema = @Schema(implementation = StudentProfileResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o RUT duplicado"),
        @ApiResponse(responseCode = "409", description = "El RUT ya existe en el sistema")
    })
    @PostMapping
    public ResponseEntity<StudentProfileResponseDTO> create(
        @Valid @RequestBody StudentRequestDTO request) {
        log.info("POST /api/v1/students - Creando nuevo estudiante");
        return new ResponseEntity<>(studentService.create(request), HttpStatus.CREATED);
    }
```
**Líneas 1-13:**
- **@Operation**: Documenta endpoint en Swagger
- **@ApiResponses**: Especifica respuestas posibles (201, 400, 409)
- **@PostMapping**: Mapea HTTP POST
- **@Valid**: Valida anotaciones en DTO (RUT, tamaño, etc.)
- **@RequestBody**: Convierte JSON a DTO automático
- **HttpStatus.CREATED**: Retorna 201 en éxito
- **log.info()**: Loguea solicitud POST

#### Endpoint: GET /api/v1/students

```java
    @Operation(summary = "Listado paginado de estudiantes para tablas")
    @ApiResponse(responseCode = "200", description = "Lista de estudiantes obtenida",
        content = @Content(mediaType = "application/json", 
                          schema = @Schema(implementation = Page.class)))
    @GetMapping
    public Page<StudentShortResponseDTO> getAll(
        @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
        @Parameter(description = "Parámetros de paginación") Pageable pageable) {
        log.debug("GET /api/v1/students - Obteniendo lista paginada con: {}", pageable);
        return studentService.findAllForTable(pageable);
    }
```
**Líneas 1-10:**
- **@GetMapping**: Mapea HTTP GET
- **@PageableDefault**: Paginación por defecto (10 items, página 0)
- **sort = "id"**: Ordena por ID
- **Pageable**: Objeto Spring Data para paginación
- Ejemplo query: `?page=1&size=20&sort=lastName,desc`

#### Endpoint: GET /api/v1/students/{id}/profile

```java
    @Operation(summary = "Obtener perfil por ID (Vista Profesor)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado",
            content = @Content(mediaType = "application/json", 
                              schema = @Schema(implementation = StudentProfileResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/{id}/profile")
    public StudentProfileResponseDTO getProfile(@PathVariable Long id) {
        log.debug("GET /api/v1/students/{}/profile - Obteniendo perfil", id);
        return studentService.findProfileById(id);
    }
```
**Línnes 1-11:**
- **@GetMapping("/{id}/profile")**: Ruta completa: `/api/v1/students/{id}/profile`
- **@PathVariable**: Extrae valor `{id}` de la URL
- Retorna vista simplificada para docentes

#### Endpoint: DELETE /api/v1/students/{id}

```java
    @Operation(summary = "Eliminar estudiante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", 
                    description = "Estudiante eliminado exitosamente"),
        @ApiResponse(responseCode = "404", 
                    description = "Estudiante no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/students/{} - Eliminando estudiante", id);
        studentService.delete(id);
    }
```
**Línea 1-11:**
- **@DeleteMapping**: Mapea HTTP DELETE
- **@ResponseStatus**: Retorna 204 No Content (sin cuerpo)
- **void**: No retorna contenido (es correctamente 204)

---

## Excepciones Personalizadas {#excepciones}

### StudentAlreadyExistsException.java

```java
@ResponseStatus(value = HttpStatus.CONFLICT)
public class StudentAlreadyExistsException extends RuntimeException {
    
    public StudentAlreadyExistsException(String message) {
        super(message);
    }
    
    public StudentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
**Línea 1:** **@ResponseStatus**: Cuando se lanza, retorna automáticamente HTTP 409
**Línea 2:** Extiende RuntimeException (unchecked)
**Línea 4-5:** Constructor con mensaje (llamado en service)
**Línea 7-9:** Constructor con causa (para debugging)

### ResourceNotFoundException.java

```java
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
**Línea 1:** Retorna HTTP 404 automáticamente
Misma estructura pero para recursos no encontrados

### BusinessValidationException.java

```java
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessValidationException extends RuntimeException {
    // ...
}
```
Retorna HTTP 400 para validaciones de negocio

---

## GlobalExceptionHandler {#manejador-excepciones}

### GlobalExceptionHandler.java

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
```
**@RestControllerAdvice**: Intercepta excepciones en todos los controladores

#### Método: handleValidationExceptions()

```java
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> 
        handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Error de validación");
        response.put("errors", errors);

        log.warn("Error de validación: {}", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
```
**Línea 1-3:**
- **@ExceptionHandler**: Intercepta `MethodArgumentNotValidException`
- Se lanza cuando @Valid falla en @RequestBody

**Línea 4-5:** Crea mapas para respuesta

**Línea 7-10:** Itera sobre errores
- **getBindingResult()**: Contiene errores de validación
- **getAllErrors()**: Todos los errores (no solo el primero)
- **getField()**: Nombre del campo con error
- **getDefaultMessage()**: Mensaje de error (del @NotBlank, etc.)

**Línea 12-15:** Construye respuesta JSON

**Línea 17-18:** Loguea y retorna 400

**Respuesta Ejemplo:**
```json
{
  "timestamp": "2026-05-02T10:30:45",
  "status": 400,
  "message": "Error de validación",
  "errors": {
    "firstName": "El nombre es requerido",
    "rut": "RUT invalido"
  }
}
```

#### Método: handleResourceNotFound()

```java
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> 
        handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());

        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
```
Intercepta 404, construye respuesta uniforme, loguea

#### Método: handleStudentAlreadyExists()

```java
    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> 
        handleStudentAlreadyExists(StudentAlreadyExistsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.CONFLICT.value());

        log.warn("Intento de crear estudiante duplicado: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
```
Intercepta 409 para duplicados

#### Método: handleGenericException()

```java
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> 
        handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Error interno del servidor");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        log.error("Error no manejado", ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
```
Catch-all para excepciones no controladas, retorna 500

---

## DTOs {#data-transfer-objects}

### StudentRequestDTO.java

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequestDTO {

    @Schema(example = "12.345.678-9", description = "RUT del estudiante")
    @NotBlank(message = "El RUT es requerido")
    @RUT
    private String rut;
```
**@Data**: Genera getters, setters, equals (Lombok)
**@Builder**: Permite construcción con patrón Builder
**@Schema**: Documenta en Swagger con ejemplo
**@NotBlank**: Validación de entrada
**@RUT**: Validador custom que verifica algoritmo chileno

### StudentProfileResponseDTO.java

```java
public class StudentProfileResponseDTO {
    private Long id;
    private String rut;
    private String fullName;
    private List<String> allergies;
    private List<EmergencyContactDTO> emergencyContacts;

    @Data
    @Builder
    public static class EmergencyContactDTO {
        private String name;
        private List<String> phoneNumbers;
        private String relationship;
    }
}
```
**Clase interna**: EmergencyContactDTO para contactos
Contiene solo datos necesarios para vista profesor (sin correo, RUT de representante)

### StudentFullResponseDTO.java

Similar a Profile pero con toda la información (incluye LegalRepresentativeDTO completos)

### StudentShortResponseDTO.java

```java
public class StudentShortResponseDTO {
    private Long id;
    private String rut;
    private String fullName;
}
```
Mínima información para tablas (optimiza transferencia)

---

## Validadores Customizados {#validadores}

### RUTValidator.java

```java
public class RUTValidator implements ConstraintValidator<RUT, String> {

    @Override
    public void initialize(RUT constraintAnnotation) {
        // no initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        String rut = value.replace(".", "").replace("-", "")
                         .toUpperCase().trim();
        if (rut.length() < 2) return false;

        String numberPart = rut.substring(0, rut.length() - 1);
        char dvChar = rut.charAt(rut.length() - 1);

        if (!numberPart.chars().allMatch(Character::isDigit)) 
            return false;

        try {
            int rutNum = Integer.parseInt(numberPart);
            char expectedDv = calculateDV(rutNum);
            return dvChar == expectedDv;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private char calculateDV(int rut) {
        int m = 0, s = 1;
        while (rut != 0) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
            rut /= 10;
        }
        if (s == 0) return 'K';
        return (char) ('0' + s - 1);
    }
}
```
**Línea 1:** Implementa `ConstraintValidator<Anotacion, TipoDato>`

**Línea 6-8:** Inicialización vacía (no necesaria)

**Línea 10-17:**
```
Normalizacion: "12.345.678-9" → "12345678 9"
Extrae números: "12345678"
Extrae dígito verificador: "9"
```

**Línea 18-22:** Valida que todos sean dígitos excepto último

**Línea 24-30:** Calcula DV esperado y compara

**Línea 32-42:** Algoritmo RUT chileno
- Multiplica cada dígito por valor (2-7 cíclico)
- Suma módulo 11
- Resultado 0 → 'K', resto 1-10 se mapea a 0-9

### PhoneValidator.java

```java
public class PhoneValidator 
    implements ConstraintValidator<Phone, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;  // opcional
        
        String phone = value.replace("+", "").replace(" ", "").trim();

        for (char c : phone.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return phone.length() >= 9 && phone.length() <= 15;
    }
}
```
**Línea 6:** null válido (campo opcional)
**Línea 8:** Normaliza: "+56 912 345 678" → "56912345678"
**Línea 10-14:** Verifica solo dígitos
**Línea 15:** Valida rango 9-15 dígitos

---

## Tests Unitarios {#tests-unitarios}

### StudentServiceTest.java

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService - Tests Unitarios")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;
```
**@ExtendWith**: Integra Mockito con JUnit 5
**@Mock**: Crea mock del repositorio y mapper
**@InjectMocks**: Inyecta mocks en StudentService

```java
    @BeforeEach
    void setUp() {
        requestDTO = StudentRequestDTO.builder()
                .rut("12.345.678-9")
                .firstName("Juan")
                // ... más campos
                .build();
    }
```
**@BeforeEach**: Ejecuta antes de cada test
Prepara datos de prueba

```java
    @Test
    @DisplayName("Debe crear un nuevo estudiante correctamente")
    void testCreateStudentSuccess() {
        // Arrange: Configura el comportamiento de los mocks
        when(studentRepository.existsByRut(requestDTO.getRut()))
            .thenReturn(false);
        when(studentMapper.requestDtoToEntity(requestDTO))
            .thenReturn(student);
        when(studentRepository.save(any(Student.class)))
            .thenReturn(student);

        // Act: Ejecuta el método a probar
        StudentProfileResponseDTO result = studentService.create(requestDTO);

        // Assert: Verifica el resultado
        assertNotNull(result);
        assertEquals("Juan García López", result.getFullName());

        // Verifica que se llamaron los métodos esperados
        verify(studentRepository).existsByRut(requestDTO.getRut());
        verify(studentRepository).save(any(Student.class));
    }
```
**AAA Pattern:**
1. **Arrange**: Configura mocks con `when().thenReturn()`
2. **Act**: Ejecuta método a probar
3. **Assert**: Verifica resultados con `assert*()` y `verify()`

**when()**: Define comportamiento del mock
**any()**: Acepta cualquier argumento
**verify()**: Verifica que se llamó el método

```java
    @Test
    @DisplayName("Debe lanzar excepción al crear estudiante con RUT duplicado")
    void testCreateStudentWithDuplicateRut() {
        when(studentRepository.existsByRut(requestDTO.getRut()))
            .thenReturn(true);

        assertThrows(StudentAlreadyExistsException.class, () -> {
            studentService.create(requestDTO);
        });

        verify(studentRepository, never())
            .save(any(Student.class));
    }
```
**never()**: Verifica que NO se llamó el método
Prueba que duplicados lanzan excepción

---

## Dockerfile {#dockerfile}

### Dockerfile Multi-Stage

```dockerfile
FROM maven:3.9-eclipse-temurin-21 as builder

LABEL maintainers="DigitalClassroom Team"

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests=true
```
**Línea 1:** Stage 1: Compilación (builder)
- **maven:3.9**: Imagen con Maven 3.9
- **eclipse-temurin-21**: JDK 21
- **as builder**: Nombre del stage

**Línea 5:** Directorio de trabajo en contenedor
**Línea 7-8:** Copia archivos del host al contenedor
**Línea 10:** Maven compila (salta tests para rapidez)

```dockerfile
FROM eclipse-temurin:21-jre-alpine

LABEL maintainers="DigitalClassroom Team"

WORKDIR /app

COPY --from=builder /app/target/student-manager-0.0.1-SNAPSHOT.jar app.jar

RUN apk add --no-cache curl

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```
**Línea 1:** Stage 2: Runtime (imagen final)
- **jre-alpine**: Solo JRE (no Maven), tamaño ~200MB vs 1GB
- Alpine: Versión Linux mínima

**Línea 6:** Copia JAR compilado del stage 1
**Línea 8:** Instala curl (requisito para healthcheck)
**Línea 10:** Expone puerto 8080 (documentación)
**Línea 12-13:** Healthcheck cada 30s, timeout 3s
- Verifica endpoint `/actuator/health`
- Docker removerá contenedor si falla

**Línea 15:** Ejecuta JAR al iniciar contenedor

**Ventajas de multi-stage:**
- Imagen final ~300MB (no incluye Maven)
- Fuente no expuesta en producción
- Compilación optimizada

---

## Application Properties {#application-properties}

```properties
spring.application.name=Student_Manager
server.port=${SERVER_PORT:8080}
server.servlet.context-path=${CONTEXT_PATH:/}
```
**Línea 1:** Nombre en logs
**Línea 2:** Puerto con variable de entorno (defecto 8080)
**Línea 3:** Path base (/ permite `/api/v1/students`)

```properties
spring.datasource.url=${DB_URL:jdbc:mariadb://localhost:3306/student_manager_db?createDatabaseIfNotExist=true}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
```
**URL:** `jdbc:mariadb://host:puerto/bd?createDatabaseIfNotExist=true`
- Crea BD si no existe
- Toda conexión usa esta URL

```properties
spring.datasource.hikari.maximum-pool-size=${DB_POOL_SIZE:10}
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
```
**HikariCP:** Pool conexiones eficiente
- Max 10 conexiones simultáneas
- Mínimo 2 inactivas
- Timeout 20s para obtener conexión

```properties
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}
spring.jpa.show-sql=${SHOW_SQL:false}
spring.jpa.properties.hibernate.format_sql=true
```
**ddl-auto: update**
- Altera tablas existentes
- Desarrollo: seguro
- Producción: usar `validate`

**show-sql:** Loguea queries SQL (DEBUG)
**format_sql:** Formatea SQL legible

```properties
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```
**Optimizaciones Hibernate:**
- **jdbc.batch_size: 20**: Agrupa 20 inserts/updates en one request
- **order_inserts: true**: Ordena inserts (mejora cache)
- **order_updates: true**: Ordena updates

```properties
logging.level.root=${LOG_LEVEL:INFO}
logging.level.cl.digitalclassroom.studentmanager=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```
**Logging:**
- Root: INFO (warnings/errors)
- StudentManager package: DEBUG (detallado)
- Formato: `2026-05-02 10:30:45 - mensaje`

```properties
springdoc.swagger-ui.path=/docs
springdoc.swagger-ui.enabled=true
springdoc.api-docs.path=/v3/api-docs
```
**Swagger:**
- UI en `/docs`
- OpenAPI JSON en `/v3/api-docs`

```properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoints.web.exposure.exclude=env,beans
management.endpoint.health.show-details=when-authorized
```
**Actuator (Monitoreo):**
- **include**: Endpoints expuestos
- **/actuator/health**: Estado aplicación
- **/actuator/metrics**: Métricas
- **/actuator/info**: Información versión
- **exclude**: Endpoints sensibles ocultos

---

## Flujo Completo: Creación de Estudiante

### 1. Cliente envía:
```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "rut": "12.345.678-9",
    "firstName": "Juan",
    "lastName": "García",
    "birthDate": "2010-05-15",
    "allergies": ["Maní"],
    "legalRepresentatives": [{...}]
  }'
```

### 2. StudentController.create() recibe:
- Deserializa JSON a `StudentRequestDTO` 
- `@Valid` ejecuta validadores (`@NotBlank`, `@RUT`, etc.)
- Si falla validación → `GlobalExceptionHandler.handleValidationExceptions()` → 400
- Si pasa → llama `studentService.create(request)`

### 3. StudentService.create() ejecuta:
```
a) studentRepository.existsByRut() → Query en BD
   ✓ Si existe → StudentAlreadyExistsException → 409
b) studentMapper.requestDtoToEntity() → Convierte DTO a Entity
c) studentRepository.save() → INSERT en tabla students
d) studentMapper.entityToProfileResponseDto() → Mapea Entity a DTO
e) Retorna DTO respuesta
```

### 4. GlobalExceptionHandler captura excepciones:
- 400: Validación
- 404: No encontrado
- 409: RUT duplicado
- 500: Error no manejado

### 5. Cliente recibe:
```json
HTTP/1.1 201 Created
{
  "id": 1,
  "rut": "12.345.678-9",
  "fullName": "Juan García",
  "allergies": ["Maní"],
  "emergencyContacts": [...]
}
```

---

## Ejecución desde Línea de Comando

### Build
```bash
# Compilar
mvn clean compile

# Tests
mvn test

# Build completo (JAR)
mvn clean package

# Skip tests
mvn clean package -DskipTests
```

### Ejecución
```bash
# Via Maven
mvn spring-boot:run

# Via JAR directo
java -jar target/student-manager-0.0.1-SNAPSHOT.jar

# Variables de entorno
DB_URL=jdbc:mariadb://db:3306/student_manager_db \
DB_USERNAME=root \
DB_PASSWORD=password \
java -jar target/student-manager-0.0.1-SNAPSHOT.jar
```

### Docker
```bash
# Build imagen
docker build -t student-manager:1.0 .

# Run contenedor
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mariadb://db:3306/student_manager_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  student-manager:1.0
```

---

## Resumen de Mejoras Implementadas

✅ **Excepciones personalizadas** (StudentAlreadyExistsException, BusinessValidationException)  
✅ **MapStruct Mapper** (conversión automática DTO-Entity)  
✅ **Security Config** (Spring Security con HTTP Basic)  
✅ **CORS Config** (acceso desde frontend)  
✅ **Auditoría** (createdAt, updatedAt automáticos)  
✅ **Paginación** (Page con Pageable)  
✅ **Logging estructurado** (@Slf4j, log.info/warn/debug)  
✅ **JavaDoc completo** (clases, métodos, parámetros)  
✅ **Swagger mejorado** (@Schema, @ApiResponse, ejemplos)  
✅ **Dockerfile multi-stage** (imagen optimizada)  
✅ **Application properties mejorado** (variables de entorno)  
✅ **Tests unitarios** (StudentServiceTest básica)  
✅ **README.md** (guía completa)  

---

**Documento generado automáticamente - Digital Classroom Project**

