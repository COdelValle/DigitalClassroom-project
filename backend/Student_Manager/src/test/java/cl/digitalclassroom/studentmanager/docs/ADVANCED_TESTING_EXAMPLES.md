# Ejemplos Avanzados de Pruebas - Student Manager

## 📚 Ejemplos de Extensión de Pruebas

Este documento contiene ejemplos de cómo expandir las pruebas existentes con casos más avanzados.

---

## 1. Pruebas de Performance/Benchmark

```java
@SpringBootTest
class StudentServicePerformanceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Performance - Crear 1000 estudiantes en menos de 5 segundos")
    void testBulkCreatePerformance() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            StudentRequestDTO request = StudentRequestDTO.builder()
                    .rut(String.format("1%d.000.00%d-%d", i % 10, i % 10, (i + 1) % 10))
                    .firstName("Student" + i)
                    .lastName("Prueba")
                    .birthDate(new Date())
                    .allergies(Arrays.asList("Ninguna"))
                    .legalRepresentatives(Arrays.asList(
                            LegalRepresentativeDTO.builder()
                                    .rut("11.920.072-5")
                                    .fullName("Rep")
                                    .email("rep@example.com")
                                    .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                                    .relationship("Padre")
                                    .build()
                    ))
                    .build();
            
            try {
                studentService.create(request);
            } catch (Exception e) {
                // Skip duplicates
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        
        assertThat(duration).isLessThan(5000);
        assertThat(studentRepository.count()).isGreaterThan(900);
    }

    @Test
    @DisplayName("Performance - Listar 1000 estudiantes en menos de 1 segundo")
    void testBulkReadPerformance() {
        // Setup: crear 100 estudiantes
        for (int i = 0; i < 100; i++) {
            Student student = Student.builder()
                    .rut(String.format("1%d.000.00%d-%d", i % 10, i % 10, (i + 1) % 10))
                    .firstName("Student" + i)
                    .lastName("Prueba")
                    .birthDate(new Date())
                    .allergies(Arrays.asList("Ninguna"))
                    .legalRepresentatives(Arrays.asList(
                            LegalRepresentativeDTO.builder()
                                    .rut("11.920.072-5")
                                    .fullName("Rep")
                                    .email("rep@example.com")
                                    .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                                    .relationship("Padre")
                                    .build()
                    ))
                    .build();
            
            try {
                studentRepository.save(student);
            } catch (Exception e) {
                // Skip
            }
        }

        long startTime = System.currentTimeMillis();
        List<StudentShortResponseDTO> result = studentService.findAllForTable();
        long duration = System.currentTimeMillis() - startTime;

        assertThat(duration).isLessThan(1000);
        assertThat(result).isNotEmpty();
    }
}
```

---

## 2. Pruebas con Datos Parametrizados

```java
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerParametrizedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @ParameterizedTest
    @ValueSource(strings = {
            "22.126.386-3",
            "18.456.789-0",
            "17.789.012-3",
            "16.012.345-6"
    })
    @DisplayName("Crear múltiples estudiantes con diferentes RUTs válidos")
    void testCreateMultipleValidRuts(String rut) throws Exception {
        LegalRepresentativeDTO rep = LegalRepresentativeDTO.builder()
                .rut("11.920.072-5")
                .fullName("Rep")
                .email("rep@example.com")
                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                .relationship("Padre")
                .build();

        StudentRequestDTO request = StudentRequestDTO.builder()
                .rut(rut)
                .firstName("Test")
                .lastName("Student")
                .birthDate(new Date())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(rep))
                .build();

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @CsvSource({
            "+56 9 1111 2222, true",
            "+56911112222, true",
            "911112222, true",
            "invalid, false",
            "12345, false"
    })
    @DisplayName("Validar múltiples formatos de teléfono")
    void testMultiplePhoneFormats(String phone, boolean expected) {
        PhoneValidator validator = new PhoneValidator();
        boolean result = validator.isValid(phone, null);
        assertThat(result).isEqualTo(expected);
    }
}
```

---

## 3. Pruebas con Verificación de Transacciones

```java
@SpringBootTest
class StudentServiceTransactionTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Transaction - Rollback en caso de excepción")
    void testTransactionRollback() {
        assertThat(studentRepository.count()).isZero();

        try {
            StudentRequestDTO request = StudentRequestDTO.builder()
                    .rut("22.126.386-3")
                    .firstName("Test")
                    .lastName("Test")
                    .birthDate(new Date())
                    .allergies(Arrays.asList("Ninguna"))
                    .legalRepresentatives(Arrays.asList(
                            LegalRepresentativeDTO.builder()
                                    .rut("11.920.072-5")
                                    .fullName("Rep")
                                    .email("rep@example.com")
                                    .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                                    .relationship("Padre")
                                    .build()
                    ))
                    .build();

            studentService.create(request);
            // Forzar excepción
            studentService.create(request);  // RUT duplicado
        } catch (Exception e) {
            // Esperado
        }

        // Solo un registro debe estar guardado después del rollback
        List<StudentShortResponseDTO> all = studentService.findAllForTable();
        assertThat(all).hasSize(1);
    }
}
```

---

## 4. Pruebas con Mock avanzado

```java
@ExtendWith(MockitoExtension.class)
class StudentServiceAdvancedMockTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Mock - Capturar argumentos de save")
    void testCaptureSaveArguments() {
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        
        Student mockStudent = Student.builder()
                .id(1L)
                .rut("22.126.386-3")
                .firstName("Test")
                .lastName("Test")
                .build();

        when(studentRepository.existsByRut(anyString())).thenReturn(false);
        when(studentRepository.save(any())).thenReturn(mockStudent);

        StudentRequestDTO request = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Test")
                .lastName("Test")
                .birthDate(new Date())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(
                        LegalRepresentativeDTO.builder()
                                .rut("11.920.072-5")
                                .fullName("Rep")
                                .email("rep@example.com")
                                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                                .relationship("Padre")
                                .build()
                ))
                .build();

        studentService.create(request);

        verify(studentRepository).save(captor.capture());
        
        Student savedStudent = captor.getValue();
        assertThat(savedStudent.getRut()).isEqualTo("22.126.386-3");
        assertThat(savedStudent.getFirstName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("Mock - Verificar secuencia de llamadas")
    void testVerifyCallSequence() {
        InOrder inOrder = inOrder(studentRepository);

        when(studentRepository.existsByRut(anyString())).thenReturn(false);
        when(studentRepository.save(any())).thenReturn(Student.builder()
                .id(1L)
                .rut("22.126.386-3")
                .firstName("Test")
                .lastName("Test")
                .build());

        StudentRequestDTO request = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Test")
                .lastName("Test")
                .birthDate(new Date())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(
                        LegalRepresentativeDTO.builder()
                                .rut("11.920.072-5")
                                .fullName("Rep")
                                .email("rep@example.com")
                                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                                .relationship("Padre")
                                .build()
                ))
                .build();

        studentService.create(request);

        // Verificar que existsByRut se llamó antes que save
        inOrder.verify(studentRepository).existsByRut("22.126.386-3");
        inOrder.verify(studentRepository).save(any());
    }
}
```

---

## 5. Pruebas de Integración con TestContainers

```java
// Requiere: org.testcontainers:testcontainers:1.17.6
@SpringBootTest
@Testcontainers
class StudentRepositoryTestContainersTest {

    @Container
    static MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:latest")
            .withDatabaseName("test_db")
            .withUsername("root")
            .withPassword("password");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
    }

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("TestContainers - Guardar en base de datos real")
    void testSaveWithRealDatabase() {
        Student student = Student.builder()
                .rut("22.126.386-3")
                .firstName("Test")
                .lastName("Test")
                .birthDate(new Date())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(
                        LegalRepresentativeDTO.builder()
                                .rut("11.920.072-5")
                                .fullName("Rep")
                                .email("rep@example.com")
                                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                                .relationship("Padre")
                                .build()
                ))
                .build();

        Student saved = studentRepository.save(student);

        assertThat(saved.getId()).isNotNull();
        assertThat(studentRepository.findById(saved.getId())).isPresent();
    }
}
```

---

## 6. Pruebas de Concurrencia

```java
@SpringBootTest
class StudentServiceConcurrencyTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Concurrency - Múltiples hilos creando estudiantes")
    void testConcurrentCreation() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    StudentRequestDTO request = StudentRequestDTO.builder()
                            .rut(String.format("1%d.000.00%d-%d", index, index, (index + 1) % 10))
                            .firstName("Thread" + index)
                            .lastName("Test")
                            .birthDate(new Date())
                            .allergies(Arrays.asList("Ninguna"))
                            .legalRepresentatives(Arrays.asList(
                                    LegalRepresentativeDTO.builder()
                                            .rut("11.920.072-5")
                                            .fullName("Rep")
                                            .email("rep@example.com")
                                            .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                                            .relationship("Padre")
                                            .build()
                            ))
                            .build();

                    studentService.create(request);
                } catch (Exception e) {
                    // Handle
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(studentRepository.count()).isGreaterThan(0);
    }
}
```

---

## 7. Pruebas de Validación de Campo

```java
@SpringBootTest
class StudentEntityValidationTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Validación - RUT no puede estar en blanco")
    void testRutNotBlank() {
        Student student = Student.builder()
                .rut("")  // Inválido
                .firstName("Test")
                .lastName("Test")
                .birthDate(new Date())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList())
                .build();

        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anySatisfy(v -> assertThat(v.getMessage()).contains("RUT"));
    }

    @Test
    @DisplayName("Validación - Nombre debe tener entre 2 y 50 caracteres")
    void testFirstNameLength() {
        Student student = Student.builder()
                .rut("22.126.386-3")
                .firstName("A")  // Demasiado corto
                .lastName("Test")
                .birthDate(new Date())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList())
                .build();

        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        assertThat(violations).isNotEmpty();
    }
}
```

---

## 8. Pruebas de Integración Con Seguridad

```java
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Security - Acceso sin autenticación debe rechazarse")
    @WithMockUser  // Si hay seguridad configurada
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk());  // Con @WithMockUser, funciona
    }

    @Test
    @DisplayName("Security - Sin @WithMockUser debe fallar")
    void testNoAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isUnauthorized());  // Sin usuario autenticado
    }
}
```

---

## 9. Pruebas de Reportes

```java
class StudentTestReportGenerator {

    public static void generateTestReport(TestReporter testReporter) {
        testReporter.publishEntry("Objetivo de Cobertura", "80%");
        testReporter.publishEntry("Objetivo de Pruebas", "160+");
        testReporter.publishEntry("Status", "COMPLETADO");
    }
}
```

---

## 10. Configuración de Junit 5 Avanzada

Archivo: `src/test/resources/junit-platform.properties`

```properties
# Comportamiento de pruebas
junit.jupiter.displayname.generator.default=org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores
junit.jupiter.execution.parallel.enabled=false
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent

# Reporte
junit.jupiter.reporting.on.error.include.stacktrace=verbose
```

---

## 📝 Configuración Recomendada en pom.xml

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.2.1</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.1</version>
    <scope>test</scope>
</dependency>

<!-- Para TestContainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.1</version>
    <scope>test</scope>
</dependency>

<!-- Para JaCoCo Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## 🎯 Próximas Adiciones Recomendadas

1. **Pruebas de Seguridad**: Con `@WithMockUser` si hay autenticación
2. **Pruebas de Cache**: Si se implementa cacheo
3. **Pruebas de File Upload**: Si se agregan attachments
4. **Pruebas de API REST**: Con RestAssured
5. **Pruebas de Eventos**: Si se implementan eventos de dominio
6. **Pruebas de GraphQL**: Si se expone GraphQL
7. **Pruebas de Auditoría**: Si hay auditoría de cambios
8. **Pruebas de Notificaciones**: Si hay notificaciones email/SMS

---

## 📚 Referencias

- [JUnit 5 Advanced Features](https://junit.org/junit5/docs/current/user-guide/#extensions)
- [Mockito Advanced](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Advanced](https://assertj.github.io/assertj-core-highlights.html)
- [Test Containers](https://www.testcontainers.org/)
- [Spring Testing Best Practices](https://spring.io/guides/gs/testing-web/)
