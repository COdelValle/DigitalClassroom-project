package cl.digitalclassroom.studentmanager.controller;

import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.entity.Student;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para StudentController
 * Carga todo el contexto de Spring Boot para pruebas de integración completas
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("StudentController Integration Tests")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentRequestDTO validStudentRequest;
    private Student testStudent;

    @BeforeEach
    void setUp() throws Exception {
        studentRepository.deleteAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = sdf.parse("2005-05-15");

        LegalRepresentativeDTO representative = LegalRepresentativeDTO.builder()
                .rut("11.920.072-5")
                .fullName("Juan Pérez")
                .email("juan.p@example.com")
                .phoneNumber(Arrays.asList("+56 9 1111 2222", "+56 9 3333 4444"))
                .relationship("Padre")
                .build();

        validStudentRequest = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Carlos")
                .middleName("Antonio")
                .lastName("García López")
                .birthDate(birthDate)
                .allergies(Arrays.asList("Polen", "Maíz"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();

        testStudent = Student.builder()
                .rut("18.456.789-K")
                .firstName("María")
                .lastName("Rodríguez")
                .birthDate(birthDate)
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();

        studentRepository.save(testStudent);
    }

    // ==================== TESTS DE CREATE ====================

    @Test
    @DisplayName("POST /api/v1/students - debe crear nuevo estudiante")
    void testCreateStudentIntegration() throws Exception {
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.rut").value("22.126.386-3"))
                .andExpect(jsonPath("$.fullName").value("Carlos García López"))
                .andExpect(jsonPath("$.allergies", hasItems("Polen", "Maíz")))
                .andExpect(jsonPath("$.emergencyContacts", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("POST /api/v1/students - debe retornar 201 Created")
    void testCreateStudentReturns201() throws Exception {
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/students - debe guardar en base de datos")
    void testCreateStudentSavesInDatabase() throws Exception {
        long countBefore = studentRepository.count();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated());

        long countAfter = studentRepository.count();
        assertThat(countAfter).isEqualTo(countBefore + 1);
    }

    @Test
    @DisplayName("POST /api/v1/students - debe rechazar RUT duplicado")
    void testCreateStudentRejectsDuplicateRut() throws Exception {
        // Primer estudiante
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated());

        // Intenta crear con mismo RUT
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/v1/students - debe rechazar RUT vacío")
    void testCreateStudentRejectsEmptyRut() throws Exception {
        StudentRequestDTO invalidRequest = StudentRequestDTO.builder()
                .rut("")  // RUT vacío
                .firstName("Carlos")
                .lastName("García López")
                .birthDate(new Date())
                .allergies(Arrays.asList("Polen"))
                .legalRepresentatives(Arrays.asList(LegalRepresentativeDTO.builder()
                        .rut("11.920.072-5")
                        .fullName("Juan")
                        .email("juan@example.com")
                        .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                        .relationship("Padre")
                        .build()))
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/v1/students - debe rechazar nombre vacío")
    void testCreateStudentRejectsEmptyName() throws Exception {
        StudentRequestDTO invalidRequest = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("")  // Nombre vacío
                .lastName("García López")
                .birthDate(new Date())
                .allergies(Arrays.asList("Polen"))
                .legalRepresentatives(Arrays.asList(LegalRepresentativeDTO.builder()
                        .rut("11.920.072-5")
                        .fullName("Juan")
                        .email("juan@example.com")
                        .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                        .relationship("Padre")
                        .build()))
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is4xxClientError());
    }

    // ==================== TESTS DE GET ALL ====================

    @Test
    @DisplayName("GET /api/v1/students - debe retornar lista de estudiantes")
    void testGetAllStudentsIntegration() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].rut").exists())
                .andExpect(jsonPath("$[0].fullName").exists());
    }

    @Test
    @DisplayName("GET /api/v1/students - debe retornar múltiples estudiantes")
    void testGetAllStudentsReturnsMultiple() throws Exception {
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(1))));
    }

    @Test
    @DisplayName("GET /api/v1/students - debe retornar lista vacía si no hay estudiantes")
    void testGetAllStudentsReturnsEmptyList() throws Exception {
        studentRepository.deleteAll();

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/students - debe retornar datos simplificados")
    void testGetAllStudentsReturnsShortData() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].rut").exists())
                .andExpect(jsonPath("$[0].fullName").exists())
                .andExpect(jsonPath("$[0].allergies").doesNotExist())
                .andExpect(jsonPath("$[0].emergencyContacts").doesNotExist());
    }

    // ==================== TESTS DE GET PROFILE ====================

    @Test
    @DisplayName("GET /api/v1/students/{id}/profile - debe retornar perfil del estudiante")
    void testGetProfileByIdIntegration() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(get("/api/v1/students/" + id + "/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.rut").value("18.456.789-K"))
                .andExpect(jsonPath("$.fullName").value("María Rodríguez"))
                .andExpect(jsonPath("$.emergencyContacts").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/profile - debe retornar 404 si no existe")
    void testGetProfileByIdReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/students/999/profile"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/profile - debe incluir contactos de emergencia")
    void testGetProfileIncludesEmergencyContacts() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(get("/api/v1/students/" + id + "/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emergencyContacts", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.emergencyContacts[0].name").exists())
                .andExpect(jsonPath("$.emergencyContacts[0].phoneNumbers").isArray())
                .andExpect(jsonPath("$.emergencyContacts[0].relationship").exists());
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/profile - debe incluir alergias")
    void testGetProfileIncludesAllergies() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(get("/api/v1/students/" + id + "/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allergies", hasItem("Ninguna")));
    }

    // ==================== TESTS DE GET FULL ====================

    @Test
    @DisplayName("GET /api/v1/students/{id}/full - debe retornar detalle completo")
    void testGetFullDetailByIdIntegration() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(get("/api/v1/students/" + id + "/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.rut").value("18.456.789-K"))
                .andExpect(jsonPath("$.firstName").value("María"))
                .andExpect(jsonPath("$.lastName").value("Rodríguez"))
                .andExpect(jsonPath("$.birthDate").exists())
                .andExpect(jsonPath("$.allergies").isArray())
                .andExpect(jsonPath("$.legalRepresentatives").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/full - debe retornar 404 si no existe")
    void testGetFullDetailByIdReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/students/999/full"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/full - debe incluir representantes legales")
    void testGetFullDetailIncludesRepresentatives() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(get("/api/v1/students/" + id + "/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalRepresentatives", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.legalRepresentatives[0].rut").exists())
                .andExpect(jsonPath("$.legalRepresentatives[0].fullName").exists())
                .andExpect(jsonPath("$.legalRepresentatives[0].email").exists());
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/full - debe incluir nombre intermedio si existe")
    void testGetFullDetailIncludesMiddleName() throws Exception {
        StudentRequestDTO requestWithMiddle = StudentRequestDTO.builder()
                .rut("17.789.012-K")
                .firstName("Pedro")
                .middleName("Luis")
                .lastName("Martínez")
                .birthDate(testStudent.getBirthDate())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(testStudent.getLegalRepresentatives())
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithMiddle)))
                .andExpect(status().isCreated());

        List<Student> students = studentRepository.findAll();
        Student createdStudent = students.stream()
                .filter(s -> s.getRut().equals("17.789.012-K"))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/api/v1/students/" + createdStudent.getId() + "/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Pedro"))
                .andExpect(jsonPath("$.middleName").value("Luis"))
                .andExpect(jsonPath("$.lastName").value("Martínez"));
    }

    // ==================== TESTS DE UPDATE ====================

    @Test
    @DisplayName("PUT /api/v1/students/{id} - debe actualizar estudiante existente")
    void testUpdateStudentIntegration() throws Exception {
        Long id = testStudent.getId();

        StudentRequestDTO updateRequest = StudentRequestDTO.builder()
                .rut("18.456.789-K")
                .firstName("María Elena")
                .lastName("Rodríguez López")
                .birthDate(testStudent.getBirthDate())
                .allergies(Arrays.asList("Mariscos"))
                .legalRepresentatives(testStudent.getLegalRepresentatives())
                .build();

        mockMvc.perform(put("/api/v1/students/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value("María Elena"))
                .andExpect(jsonPath("$.lastName").value("Rodríguez López"));
    }

    @Test
    @DisplayName("PUT /api/v1/students/{id} - debe retornar 404 si ID no existe")
    void testUpdateStudentReturns404() throws Exception {
        StudentRequestDTO updateRequest = StudentRequestDTO.builder()
                .rut("18.456.789-K")
                .firstName("Test")
                .lastName("Test")
                .birthDate(new Date())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(LegalRepresentativeDTO.builder()
                        .rut("11.920.072-5")
                        .fullName("Juan")
                        .email("juan@example.com")
                        .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                        .relationship("Padre")
                        .build()))
                .build();

        mockMvc.perform(put("/api/v1/students/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("PUT /api/v1/students/{id} - debe actualizar en base de datos")
    void testUpdateStudentUpdatesDatabase() throws Exception {
        Long id = testStudent.getId();

        StudentRequestDTO updateRequest = StudentRequestDTO.builder()
                .rut("18.456.789-K")
                .firstName("Updated Name")
                .lastName("Updated Last")
                .birthDate(testStudent.getBirthDate())
                .allergies(Arrays.asList("Mariscos"))
                .legalRepresentatives(testStudent.getLegalRepresentatives())
                .build();

        mockMvc.perform(put("/api/v1/students/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        Student updated = studentRepository.findById(id).orElseThrow();
        assertThat(updated.getFirstName()).isEqualTo("Updated Name");
        assertThat(updated.getLastName()).isEqualTo("Updated Last");
    }

    @Test
    @DisplayName("PUT /api/v1/students/{id} - debe rechazar actualización con datos inválidos")
    void testUpdateStudentRejectsInvalidData() throws Exception {
        Long id = testStudent.getId();

        StudentRequestDTO invalidRequest = StudentRequestDTO.builder()
                .rut("")  // RUT vacío
                .firstName("Test")
                .lastName("Test")
                .birthDate(new Date())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(LegalRepresentativeDTO.builder()
                        .rut("11.920.072-5")
                        .fullName("Juan")
                        .email("juan@example.com")
                        .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                        .relationship("Padre")
                        .build()))
                .build();

        mockMvc.perform(put("/api/v1/students/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is4xxClientError());
    }

    // ==================== TESTS DE DELETE ====================

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - debe eliminar estudiante existente")
    void testDeleteStudentIntegration() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(delete("/api/v1/students/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - debe eliminar de base de datos")
    void testDeleteStudentRemovesFromDatabase() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(delete("/api/v1/students/" + id))
                .andExpect(status().isNoContent());

        assertThat(studentRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - debe retornar 404 si ID no existe")
    void testDeleteStudentReturns404() throws Exception {
        mockMvc.perform(delete("/api/v1/students/999"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - no debe ser posible eliminarlo dos veces")
    void testDeleteStudentCannotDeleteTwice() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(delete("/api/v1/students/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/students/" + id))
                .andExpect(status().is4xxClientError());
    }

    // ==================== TESTS DE EXISTS ====================

    @Test
    @DisplayName("GET /api/v1/students/{id}/exists - debe retornar true si existe")
    void testExistsReturnsTrue() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(get("/api/v1/students/" + id + "/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/exists - debe retornar false si no existe")
    void testExistsReturnsFalse() throws Exception {
        mockMvc.perform(get("/api/v1/students/999/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/exists - debe verificar después de crear")
    void testExistsAfterCreate() throws Exception {
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validStudentRequest)))
                .andExpect(status().isCreated());

        List<Student> students = studentRepository.findAll();
        Student created = students.stream()
                .filter(s -> s.getRut().equals("22.126.386-3"))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/api/v1/students/" + created.getId() + "/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/exists - debe verificar después de eliminar")
    void testExistsAfterDelete() throws Exception {
        Long id = testStudent.getId();

        mockMvc.perform(delete("/api/v1/students/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/students/" + id + "/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ==================== Comentario para importación ====================
    // Para que funcione ArrayList en los tests
    private static class ArrayList extends java.util.ArrayList {}
}
