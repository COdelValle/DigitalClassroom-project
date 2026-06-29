package cl.digitalclassroom.studentmanager.controller;

import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de escenarios complejos para StudentController
 * Valida procesos de negocio completos E2E
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("StudentController E2E Scenario Tests")
class StudentControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentRequestDTO validStudentRequest;

    @BeforeEach
    void setUp() throws Exception {
        studentRepository.deleteAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = sdf.parse("2005-05-15");

        LegalRepresentativeDTO representative = LegalRepresentativeDTO.builder()
                .rut("11.920.072-5")
                .fullName("Juan Pérez")
                .email("juan.p@example.com")
                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                .relationship("Padre")
                .build();

        validStudentRequest = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Carlos")
                .lastName("García López")
                .birthDate(birthDate)
                .allergies(Arrays.asList("Polen"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();
    }

    // ==================== ESCENARIO 1: CREAR Y CONSULTAR ====================

    @Test
    @DisplayName("E2E - Crear estudiante y consultar por ID")
    void scenarioCreateAndQuery() throws Exception {
        // 1. Crear estudiante
        var createResponse = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // 2. Extraer ID
        String responseBody = createResponse.getResponse().getContentAsString();
        Long studentId = objectMapper.readTree(responseBody).get("id").asLong();

        // 3. Consultar por ID
        mockMvc.perform(get("/api/v1/students/" + studentId + "/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.rut").value("22.126.386-3"));
    }

    @Test
    @DisplayName("E2E - Crear, listar y verificar existencia")
    void scenarioCreateListAndExists() throws Exception {
        // 1. Crear
        var createResponse = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResponse.getResponse().getContentAsString();
        Long studentId = objectMapper.readTree(responseBody).get("id").asLong();

        // 2. Listar
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 3. Verificar existencia
        mockMvc.perform(get("/api/v1/students/" + studentId + "/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    // ==================== ESCENARIO 2: CREAR, ACTUALIZAR Y ELIMINAR ====================

    @Test
    @DisplayName("E2E - Crear, actualizar, consultar y eliminar")
    void scenarioCreateUpdateQueryDelete() throws Exception {
        // 1. Crear
        var createResponse = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResponse.getResponse().getContentAsString();
        Long studentId = objectMapper.readTree(responseBody).get("id").asLong();

        // 2. Actualizar
        StudentRequestDTO updateRequest = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Carlos Actualizado")
                .lastName("García López")
                .birthDate(validStudentRequest.getBirthDate())
                .allergies(Arrays.asList("Mariscos"))
                .legalRepresentatives(validStudentRequest.getLegalRepresentatives())
                .build();

        mockMvc.perform(put("/api/v1/students/" + studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Carlos Actualizado"));

        // 3. Consultar
        mockMvc.perform(get("/api/v1/students/" + studentId + "/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Carlos Actualizado"));

        // 4. Eliminar
        mockMvc.perform(delete("/api/v1/students/" + studentId))
                .andExpect(status().isNoContent());

        // 5. Verificar que no existe
        mockMvc.perform(get("/api/v1/students/" + studentId + "/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ==================== ESCENARIO 3: MÚLTIPLES ESTUDIANTES ====================

    @Test
    @DisplayName("E2E - Crear múltiples estudiantes y listar")
    void scenarioCreateMultipleAndList() throws Exception {
        LegalRepresentativeDTO rep = LegalRepresentativeDTO.builder()
                .rut("11.920.072-5")
                .fullName("Representante")
                .email("rep@example.com")
                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                .relationship("Padre")
                .build();

        // 1. Crear primer estudiante
        StudentRequestDTO student1 = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Carlos")
                .lastName("García")
                .birthDate(validStudentRequest.getBirthDate())
                .allergies(Arrays.asList("Polen"))
                .legalRepresentatives(Arrays.asList(rep))
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isCreated());

        // 2. Crear segundo estudiante
        StudentRequestDTO student2 = StudentRequestDTO.builder()
                .rut("18.456.789-K")
                .firstName("María")
                .lastName("Rodríguez")
                .birthDate(validStudentRequest.getBirthDate())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(rep))
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student2)))
                .andExpect(status().isCreated());

        // 3. Listar todos
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName", anyOf(
                        is("Carlos García"),
                        is("María Rodríguez"))))
                .andExpect(jsonPath("$[1].fullName", anyOf(
                        is("Carlos García"),
                        is("María Rodríguez"))));
    }

    // ==================== ESCENARIO 4: VALIDACIONES ====================

    @Test
    @DisplayName("E2E - Múltiples intentos de creación con RUT duplicado deben fallar")
    void scenarioDuplicateRutPrevention() throws Exception {
        // 1. Crear primer estudiante
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated());

        // 2. Intente crear con mismo RUT
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().is4xxClientError());

        // 3. Verificar que solo uno existe
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("E2E - Datos inválidos deben rechazarse en creación")
    void scenarioInvalidDataValidation() throws Exception {
        // Intento 1: RUT vacío
        StudentRequestDTO invalidRut = StudentRequestDTO.builder()
                .rut("")
                .firstName("Carlos")
                .lastName("García")
                .birthDate(validStudentRequest.getBirthDate())
                .allergies(Arrays.asList("Polen"))
                .legalRepresentatives(validStudentRequest.getLegalRepresentatives())
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRut)))
                .andExpect(status().is4xxClientError());

        // Intento 2: Nombre vacío
        StudentRequestDTO invalidName = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("")
                .lastName("García")
                .birthDate(validStudentRequest.getBirthDate())
                .allergies(Arrays.asList("Polen"))
                .legalRepresentatives(validStudentRequest.getLegalRepresentatives())
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidName)))
                .andExpect(status().is4xxClientError());

        // Intento 3: Representantes vacío
        StudentRequestDTO invalidReps = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Carlos")
                .lastName("García")
                .birthDate(validStudentRequest.getBirthDate())
                .allergies(Arrays.asList("Polen"))
                .legalRepresentatives(Arrays.asList())
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReps)))
                .andExpect(status().is4xxClientError());

        // Verificar que nada se creó
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== ESCENARIO 5: FLUJO DE CONSULTAS ====================

    @Test
    @DisplayName("E2E - Consultar perfil vs vista completa")
    void scenarioProfileVsFullView() throws Exception {
        // 1. Crear
        var createResponse = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResponse.getResponse().getContentAsString();
        Long studentId = objectMapper.readTree(responseBody).get("id").asLong();

        // 2. Consultar perfil (usuario profesor)
        mockMvc.perform(get("/api/v1/students/" + studentId + "/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullName").exists())
                .andExpect(jsonPath("$.emergencyContacts").exists())
                .andExpect(jsonPath("$.firstName").doesNotExist())  // No debe estar
                .andExpect(jsonPath("$.middleName").doesNotExist())  // No debe estar
                .andExpect(jsonPath("$.legalRepresentatives").doesNotExist());  // No debe estar

        // 3. Consultar completo (usuario admin)
        mockMvc.perform(get("/api/v1/students/" + studentId + "/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.legalRepresentatives").exists());
    }

    // ==================== ESCENARIO 6: STRESS TEST ====================

    @Test
    @DisplayName("E2E - Crear 10 estudiantes y listar")
    void scenarioManyStudents() throws Exception {
        LegalRepresentativeDTO rep = LegalRepresentativeDTO.builder()
                .rut("11.920.072-5")
                .fullName("Representante")
                .email("rep@example.com")
                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                .relationship("Padre")
                .build();

        // Crear 10 estudiantes
        for (int i = 0; i < 10; i++) {
            StudentRequestDTO student = StudentRequestDTO.builder()
                    .rut(buildValidRut(10_000_000 + i))
                    .firstName("Estudiante" + i)
                    .lastName("Prueba")
                    .birthDate(validStudentRequest.getBirthDate())
                    .allergies(Arrays.asList("Ninguna"))
                    .legalRepresentatives(Arrays.asList(rep))
                    .build();

            mockMvc.perform(post("/api/v1/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(student)))
                    .andExpect(status().isCreated());
        }

        // Listar y verificar que hay 10
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    // ==================== ESCENARIO 7: ACTUALIZACIÓN PARCIAL ====================

    @Test
    @DisplayName("E2E - Actualizar solo ciertos campos")
    void scenarioPartialUpdate() throws Exception {
        // 1. Crear
        var createResponse = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResponse.getResponse().getContentAsString();
        Long studentId = objectMapper.readTree(responseBody).get("id").asLong();

        // 2. Actualizar solo alergias
        StudentRequestDTO updateOnlyAllergies = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Carlos")
                .lastName("García López")
                .birthDate(validStudentRequest.getBirthDate())
                .allergies(Arrays.asList("Mariscos", "Frutos secos"))
                .legalRepresentatives(validStudentRequest.getLegalRepresentatives())
                .build();

        mockMvc.perform(put("/api/v1/students/" + studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOnlyAllergies)))
                .andExpect(status().isOk());

        // 3. Verificar cambio
        mockMvc.perform(get("/api/v1/students/" + studentId + "/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allergies", hasItems("Mariscos", "Frutos secos")))
                .andExpect(jsonPath("$.allergies", hasSize(2)));
    }

    // ==================== ESCENARIO 8: OPERACIONES NO ENCONTRADAS ====================

    @Test
    @DisplayName("E2E - Operaciones con IDs inexistentes deben retornar 404")
    void scenarioNotFoundOperations() throws Exception {
        Long nonExistentId = 99999L;

        // Get Profile
        mockMvc.perform(get("/api/v1/students/" + nonExistentId + "/profile"))
                .andExpect(status().is4xxClientError());

        // Get Full
        mockMvc.perform(get("/api/v1/students/" + nonExistentId + "/full"))
                .andExpect(status().is4xxClientError());

        // Update
        mockMvc.perform(put("/api/v1/students/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().is4xxClientError());

        // Delete
        mockMvc.perform(delete("/api/v1/students/" + nonExistentId))
                .andExpect(status().is4xxClientError());

        // Exists retorna false además
        mockMvc.perform(get("/api/v1/students/" + nonExistentId + "/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    private static String buildValidRut(int number) {
        String formattedNumber = String.format("%,d", number).replace(',', '.');
        int rut = number;
        int m = 0;
        int s = 1;
        while (rut != 0) {
            s = (s + (rut % 10) * (9 - m++ % 6)) % 11;
            rut /= 10;
        }
        char dv = s == 0 ? 'K' : (char) ('0' + s - 1);
        return formattedNumber + "-" + dv;
    }

    // Clase auxiliar para ArrayList
    private static class ArrayList extends java.util.ArrayList {}
}
