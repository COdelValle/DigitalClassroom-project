package cl.digitalclassroom.studentmanager.controller;

import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentFullResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentProfileResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentShortResponseDTO;
import cl.digitalclassroom.studentmanager.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para StudentController
 * Utiliza Mockito para simular las dependencias del servicio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentController Unit Tests")
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private MockMvc mockMvc;
    private StudentRequestDTO validStudentRequest;
    private StudentProfileResponseDTO studentProfileResponse;
    private StudentShortResponseDTO studentShortResponse;
    private StudentFullResponseDTO studentFullResponse;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
        initializeTestData();
    }

    private void initializeTestData() throws Exception {
        // Crear estudiante de prueba
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

        studentProfileResponse = StudentProfileResponseDTO.builder()
                .id(1L)
                .rut("22.126.386-3")
                .fullName("Carlos García López")
                .allergies(Arrays.asList("Polen", "Maíz"))
                .emergencyContacts(Arrays.asList(
                        StudentProfileResponseDTO.EmergencyContactDTO.builder()
                                .name("Juan Pérez")
                                .phoneNumbers(Arrays.asList("+56 9 1111 2222", "+56 9 3333 4444"))
                                .relationship("Padre")
                                .build()
                ))
                .build();

        studentShortResponse = StudentShortResponseDTO.builder()
                .id(1L)
                .rut("22.126.386-3")
                .fullName("Carlos García López")
                .build();

        studentFullResponse = StudentFullResponseDTO.builder()
                .id(1L)
                .rut("22.126.386-3")
                .firstName("Carlos")
                .middleName("Antonio")
                .lastName("García López")
                .birthDate(birthDate)
                .allergies(Arrays.asList("Polen", "Maíz"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();
    }

    // ==================== TESTS DE CREATE ====================

    @Test
    @DisplayName("Create - debe criar nuevo estudiante correctamente")
    void testCreateStudentSuccess() {
        when(studentService.create(any(StudentRequestDTO.class)))
                .thenReturn(studentProfileResponse);

        ResponseEntity<StudentProfileResponseDTO> response = studentController.create(validStudentRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getRut()).isEqualTo("22.126.386-3");
        assertThat(response.getBody().getFullName()).isEqualTo("Carlos García López");

        verify(studentService, times(1)).create(any(StudentRequestDTO.class));
    }

    @Test
    @DisplayName("Create - debe tener status CREATED (201)")
    void testCreateStudentReturnsCreatedStatus() {
        when(studentService.create(any(StudentRequestDTO.class)))
                .thenReturn(studentProfileResponse);

        ResponseEntity<StudentProfileResponseDTO> response = studentController.create(validStudentRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    @DisplayName("Create - debería retornar StudentProfileResponseDTO")
    void testCreateStudentReturnsCorrectDto() {
        when(studentService.create(any(StudentRequestDTO.class)))
                .thenReturn(studentProfileResponse);

        ResponseEntity<StudentProfileResponseDTO> response = studentController.create(validStudentRequest);

        assertThat(response.getBody())
                .isInstanceOf(StudentProfileResponseDTO.class);
    }

    @Test
    @DisplayName("Create - debe invocar al servicio una sola vez")
    void testCreateStudentCallsServiceOnce() {
        when(studentService.create(any(StudentRequestDTO.class)))
                .thenReturn(studentProfileResponse);

        studentController.create(validStudentRequest);

        verify(studentService, times(1)).create(any(StudentRequestDTO.class));
        verifyNoMoreInteractions(studentService);
    }

    // ==================== TESTS DE GET ALL ====================

    @Test
    @DisplayName("GetAll - debe retornar lista de estudiantes")
    void testGetAllStudentsSuccess() {
        List<StudentShortResponseDTO> expectedList = Arrays.asList(
                studentShortResponse,
                StudentShortResponseDTO.builder()
                        .id(2L)
                        .rut("18.456.789-K")
                        .fullName("María Rodríguez")
                        .build()
        );

        when(studentService.findAllForTable()).thenReturn(expectedList);

        List<StudentShortResponseDTO> response = studentController.getAll();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(1).getId()).isEqualTo(2L);

        verify(studentService, times(1)).findAllForTable();
    }

    @Test
    @DisplayName("GetAll - debe retornar lista vacía si no hay estudiantes")
    void testGetAllStudentsEmptyList() {
        when(studentService.findAllForTable()).thenReturn(Arrays.asList());

        List<StudentShortResponseDTO> response = studentController.getAll();

        assertThat(response).isEmpty();
        verify(studentService, times(1)).findAllForTable();
    }

    @Test
    @DisplayName("GetAll - debe retornar StudentShortResponseDTO sin datos completos")
    void testGetAllStudentsReturnsShortDto() {
        List<StudentShortResponseDTO> expectedList = Arrays.asList(studentShortResponse);

        when(studentService.findAllForTable()).thenReturn(expectedList);

        List<StudentShortResponseDTO> response = studentController.getAll();

        assertThat(response.get(0).getFullName()).isEqualTo("Carlos García López");
        assertThat(response.get(0).getRut()).isEqualTo("22.126.386-3");
    }

    // ==================== TESTS DE GET PROFILE ====================

    @Test
    @DisplayName("GetProfile - debe retornar perfil del estudiante por ID")
    void testGetProfileByIdSuccess() {
        when(studentService.findProfileById(1L))
                .thenReturn(studentProfileResponse);

        StudentProfileResponseDTO response = studentController.getProfile(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFullName()).isEqualTo("Carlos García López");
        assertThat(response.getEmergencyContacts()).isNotEmpty();

        verify(studentService, times(1)).findProfileById(1L);
    }

    @Test
    @DisplayName("GetProfile - debe incluir contactos de emergencia")
    void testGetProfileIncludesEmergencyContacts() {
        when(studentService.findProfileById(1L))
                .thenReturn(studentProfileResponse);

        StudentProfileResponseDTO response = studentController.getProfile(1L);

        assertThat(response.getEmergencyContacts()).hasSize(1);
        assertThat(response.getEmergencyContacts().get(0).getName()).isEqualTo("Juan Pérez");
        assertThat(response.getEmergencyContacts().get(0).getRelationship()).isEqualTo("Padre");
    }

    @Test
    @DisplayName("GetProfile - debe incluir alergias")
    void testGetProfileIncludesAllergies() {
        when(studentService.findProfileById(1L))
                .thenReturn(studentProfileResponse);

        StudentProfileResponseDTO response = studentController.getProfile(1L);

        assertThat(response.getAllergies()).contains("Polen", "Maíz");
    }

    // ==================== TESTS DE GET FULL ====================

    @Test
    @DisplayName("GetFull - debe retornar detalle completo del estudiante")
    void testGetFullDetailByIdSuccess() {
        when(studentService.findFullDetailById(1L))
                .thenReturn(studentFullResponse);

        StudentFullResponseDTO response = studentController.getFull(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Carlos");
        assertThat(response.getMiddleName()).isEqualTo("Antonio");
        assertThat(response.getLastName()).isEqualTo("García López");

        verify(studentService, times(1)).findFullDetailById(1L);
    }

    @Test
    @DisplayName("GetFull - debe incluir representantes legales")
    void testGetFullDetailIncludesLegalRepresentatives() {
        when(studentService.findFullDetailById(1L))
                .thenReturn(studentFullResponse);

        StudentFullResponseDTO response = studentController.getFull(1L);

        assertThat(response.getLegalRepresentatives()).isNotEmpty();
        assertThat(response.getLegalRepresentatives().get(0).getFullName()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("GetFull - debe obtener información completa del estudiante")
    void testGetFullDetailReturnsCompleteInfo() {
        when(studentService.findFullDetailById(1L))
                .thenReturn(studentFullResponse);

        StudentFullResponseDTO response = studentController.getFull(1L);

        assertThat(response.getRut()).isEqualTo("22.126.386-3");
        assertThat(response.getAllergies()).isNotEmpty();
        assertThat(response.getBirthDate()).isNotNull();
    }

    // ==================== TESTS DE UPDATE ====================

    @Test
    @DisplayName("Update - debe actualizar estudiante exitosamente")
    void testUpdateStudentSuccess() {
        when(studentService.update(eq(1L), any(StudentRequestDTO.class)))
                .thenReturn(studentFullResponse);

        StudentFullResponseDTO response = studentController.update(1L, validStudentRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Carlos");

        verify(studentService, times(1)).update(eq(1L), any(StudentRequestDTO.class));
    }

    @Test
    @DisplayName("Update - debe retornar StudentFullResponseDTO después de actualizar")
    void testUpdateStudentReturnsFullDto() {
        when(studentService.update(eq(1L), any(StudentRequestDTO.class)))
                .thenReturn(studentFullResponse);

        StudentFullResponseDTO response = studentController.update(1L, validStudentRequest);

        assertThat(response)
                .isInstanceOf(StudentFullResponseDTO.class);
    }

    @Test
    @DisplayName("Update - debe enviar el ID correcto al servicio")
    void testUpdateStudentSendsCorrectId() {
        when(studentService.update(eq(1L), any(StudentRequestDTO.class)))
                .thenReturn(studentFullResponse);

        studentController.update(1L, validStudentRequest);

        verify(studentService, times(1)).update(eq(1L), any(StudentRequestDTO.class));
    }

    @Test
    @DisplayName("Update - debe modificar los datos del estudiante")
    void testUpdateStudentModifiesData() {
        when(studentService.update(eq(1L), any(StudentRequestDTO.class)))
                .thenReturn(studentFullResponse);

        StudentFullResponseDTO response = studentController.update(1L, validStudentRequest);

        assertThat(response.getFirstName()).isEqualTo("Carlos");
        assertThat(response.getRut()).isEqualTo("22.126.386-3");
    }

    // ==================== TESTS DE DELETE ====================

    @Test
    @DisplayName("Delete - debe eliminar estudiante correctamente")
    void testDeleteStudentSuccess() {
        doNothing().when(studentService).delete(1L);

        studentController.delete(1L);

        verify(studentService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Delete - debe llamar al servicio con el ID correcto")
    void testDeleteStudentCallsServiceWithCorrectId() {
        doNothing().when(studentService).delete(2L);

        studentController.delete(2L);

        verify(studentService, times(1)).delete(2L);
        verifyNoMoreInteractions(studentService);
    }

    @Test
    @DisplayName("Delete - debe invocar delete una sola vez")
    void testDeleteStudentCallsServiceOnce() {
        doNothing().when(studentService).delete(1L);

        studentController.delete(1L);

        verify(studentService, times(1)).delete(1L);
    }

    // ==================== TESTS DE EXISTS ====================

    @Test
    @DisplayName("Exists - debe retornar true si estudiante existe")
    void testExistsReturnsTrue() {
        when(studentService.exist(1L)).thenReturn(true);

        boolean response = studentController.exists(1L);

        assertThat(response).isTrue();
        verify(studentService, times(1)).exist(1L);
    }

    @Test
    @DisplayName("Exists - debe retornar false si estudiante no existe")
    void testExistsReturnsFalse() {
        when(studentService.exist(999L)).thenReturn(false);

        boolean response = studentController.exists(999L);

        assertThat(response).isFalse();
        verify(studentService, times(1)).exist(999L);
    }

    @Test
    @DisplayName("Exists - debe verificar existencia correctamente")
    void testExistsVerifiesCorrectly() {
        when(studentService.exist(1L)).thenReturn(true);

        boolean response = studentController.exists(1L);

        assertThat(response).isTrue();
    }

    // ==================== TESTS DE ERROR HANDLING ====================

    @Test
    @DisplayName("Create - debe propagarse excepción del servicio")
    void testCreateStudentHandlesServiceException() {
        when(studentService.create(any(StudentRequestDTO.class)))
                .thenThrow(new RuntimeException("RUT duplicado"));

        try {
            studentController.create(validStudentRequest);
            org.junit.jupiter.api.Assertions.fail("Debería lanzar excepción");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("RUT duplicado");
            verify(studentService, times(1)).create(any(StudentRequestDTO.class));
        }
    }

    @Test
    @DisplayName("GetProfile - debe propagarse excepción cuando no existe")
    void testGetProfileHandlesNotFound() {
        when(studentService.findProfileById(999L))
                .thenThrow(new RuntimeException("Estudiante no encontrado"));

        try {
            studentController.getProfile(999L);
            org.junit.jupiter.api.Assertions.fail("Debería lanzar excepción");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Estudiante no encontrado");
        }
    }

    @Test
    @DisplayName("Update - debe propagarse excepción cuando ID no existe")
    void testUpdateHandlesNotFound() {
        when(studentService.update(eq(999L), any(StudentRequestDTO.class)))
                .thenThrow(new RuntimeException("ID no existe"));

        try {
            studentController.update(999L, validStudentRequest);
            org.junit.jupiter.api.Assertions.fail("Debería lanzar excepción");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("ID no existe");
        }
    }

    @Test
    @DisplayName("Delete - debe propagarse excepción cuando ID no existe")
    void testDeleteHandlesNotFound() {
        doThrow(new RuntimeException("No se puede borrar, ID no existe: 999"))
                .when(studentService).delete(999L);

        try {
            studentController.delete(999L);
            org.junit.jupiter.api.Assertions.fail("Debería lanzar excepción");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("ID no existe");
        }
    }
}
