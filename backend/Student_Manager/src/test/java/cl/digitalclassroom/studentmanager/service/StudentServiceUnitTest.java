package cl.digitalclassroom.studentmanager.service;

import cl.digitalclassroom.studentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentFullResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentProfileResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentShortResponseDTO;
import cl.digitalclassroom.studentmanager.model.entity.Student;
import cl.digitalclassroom.studentmanager.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para StudentService
 * Utiliza Mockito para simular las dependencias del repositorio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceUnitTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private StudentRequestDTO studentRequest;
    private Student student;
    private LegalRepresentativeDTO legalRep;

    @BeforeEach
    void setUp() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = sdf.parse("2005-05-15");

        legalRep = LegalRepresentativeDTO.builder()
                .rut("11.920.072-5")
                .fullName("Juan Pérez")
                .email("juan.p@example.com")
                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                .relationship("Padre")
                .build();

        studentRequest = StudentRequestDTO.builder()
                .rut("22.126.386-3")
                .firstName("Carlos")
                .middleName("Antonio")
                .lastName("García López")
                .birthDate(birthDate)
                .allergies(Arrays.asList("Polen", "Maíz"))
                .legalRepresentatives(Arrays.asList(legalRep))
                .build();

        student = Student.builder()
                .id(1L)
                .rut("22.126.386-3")
                .firstName("Carlos")
                .middleName("Antonio")
                .lastName("García López")
                .birthDate(birthDate)
                .allergies(Arrays.asList("Polen", "Maíz"))
                .legalRepresentatives(Arrays.asList(legalRep))
                .build();
    }

    // ==================== TESTS DE CREATE ====================

    @Test
    @DisplayName("Create - debe crear estudiante con datos válidos")
    void testCreateStudentSuccess() {
        when(studentRepository.existsByRut(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentProfileResponseDTO result = studentService.create(studentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRut()).isEqualTo("22.126.386-3");
        assertThat(result.getFullName()).isEqualTo("Carlos García López");

        verify(studentRepository, times(1)).existsByRut("22.126.386-3");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Create - debe lanzar excepción si RUT ya existe")
    void testCreateStudentWithDuplicateRut() {
        when(studentRepository.existsByRut("22.126.386-3")).thenReturn(true);

        assertThatThrownBy(() -> studentService.create(studentRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un estudiante con el RUT");

        verify(studentRepository, times(1)).existsByRut("22.126.386-3");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Create - debe mapear correctamente los datos")
    void testCreateMapsCorrectly() {
        when(studentRepository.existsByRut(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentProfileResponseDTO result = studentService.create(studentRequest);

        assertThat(result.getEmergencyContacts()).isNotEmpty();
        assertThat(result.getAllergies()).contains("Polen", "Maíz");
    }

    @Test
    @DisplayName("Create - debe guardar en repositorio una sola vez")
    void testCreateCallsSaveOnce() {
        when(studentRepository.existsByRut(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        studentService.create(studentRequest);

        verify(studentRepository, times(1)).save(any(Student.class));
        verifyNoMoreInteractions(studentRepository);
    }

    // ==================== TESTS DE FIND ALL ====================

    @Test
    @DisplayName("FindAllForTable - debe retornar todos los estudiantes")
    void testFindAllForTableSuccess() {
        Student student2 = Student.builder()
                .id(2L)
                .rut("18.456.789-0")
                .firstName("María")
                .lastName("Rodríguez")
                .build();

        when(studentRepository.findAll())
                .thenReturn(Arrays.asList(student, student2));

        List<StudentShortResponseDTO> result = studentService.findAllForTable();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);

        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("FindAllForTable - debe retornar lista vacía si no hay estudiantes")
    void testFindAllForTableEmpty() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList());

        List<StudentShortResponseDTO> result = studentService.findAllForTable();

        assertThat(result).isEmpty();
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("FindAllForTable - debe retornar StudentShortResponseDTO")
    void testFindAllForTableReturnsShortDto() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student));

        List<StudentShortResponseDTO> result = studentService.findAllForTable();

        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getRut()).isEqualTo("22.126.386-3");
        assertThat(result.get(0).getFullName()).isEqualTo("Carlos García López");
    }

    // ==================== TESTS DE FIND PROFILE BY ID ====================

    @Test
    @DisplayName("FindProfileById - debe retornar perfil del estudiante")
    void testFindProfileByIdSuccess() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentProfileResponseDTO result = studentService.findProfileById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRut()).isEqualTo("22.126.386-3");

        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("FindProfileById - debe lanzar excepción si no existe")
    void testFindProfileByIdNotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.findProfileById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Estudiante no encontrado con ID: 999");

        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("FindProfileById - debe incluir contactos de emergencia")
    void testFindProfileByIdIncludesEmergencyContacts() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentProfileResponseDTO result = studentService.findProfileById(1L);

        assertThat(result.getEmergencyContacts()).isNotEmpty();
        assertThat(result.getEmergencyContacts().get(0).getName()).isEqualTo("Juan Pérez");
    }

    // ==================== TESTS DE FIND FULL DETAIL ====================

    @Test
    @DisplayName("FindFullDetailById - debe retornar detalle completo del estudiante")
    void testFindFullDetailByIdSuccess() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentFullResponseDTO result = studentService.findFullDetailById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Carlos");
        assertThat(result.getMiddleName()).isEqualTo("Antonio");
        assertThat(result.getLastName()).isEqualTo("García López");

        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("FindFullDetailById - debe lanzar excepción si no existe")
    void testFindFullDetailByIdNotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.findFullDetailById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Estudiante no encontrado con ID: 999");

        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("FindFullDetailById - debe incluir representantes legales")
    void testFindFullDetailByIdIncludesRepresentatives() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentFullResponseDTO result = studentService.findFullDetailById(1L);

        assertThat(result.getLegalRepresentatives()).isNotEmpty();
        assertThat(result.getLegalRepresentatives().get(0).getFullName()).isEqualTo("Juan Pérez");
    }

    // ==================== TESTS DE UPDATE ====================

    @Test
    @DisplayName("Update - debe actualizar estudiante existente")
    void testUpdateStudentSuccess() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentFullResponseDTO result = studentService.update(1L, studentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Carlos");

        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Update - debe lanzar excepción si ID no existe")
    void testUpdateStudentNotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.update(999L, studentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se puede actualizar, ID no existe: 999");

        verify(studentRepository, times(1)).findById(999L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Update - debe preservar RUT original")
    void testUpdatePreservesOriginalRut() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentRequestDTO updateRequest = StudentRequestDTO.builder()
                .rut("99.999.999-9")  // RUT diferente
                .firstName("Carlos")
                .lastName("García López")
                .birthDate(student.getBirthDate())
                .allergies(Arrays.asList("Polen"))
                .legalRepresentatives(Arrays.asList(legalRep))
                .build();

        studentService.update(1L, updateRequest);

        verify(studentRepository).save(any(Student.class));
    }

    // ==================== TESTS DE DELETE ====================

    @Test
    @DisplayName("Delete - debe eliminar estudiante existente")
    void testDeleteStudentSuccess() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        studentService.delete(1L);

        verify(studentRepository, times(1)).existsById(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Delete - debe lanzar excepción si ID no existe")
    void testDeleteStudentNotFound() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se puede borrar, ID no existe: 999");

        verify(studentRepository, times(1)).existsById(999L);
        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Delete - debe verificar existencia antes de borrar")
    void testDeleteVerifiesExistenceFirst() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        studentService.delete(1L);

        verify(studentRepository, times(1)).existsById(1L);
    }

    // ==================== TESTS DE EXISTS ====================

    @Test
    @DisplayName("Exist - debe retornar true si existe")
    void testExistReturnsTrue() {
        when(studentRepository.existsById(1L)).thenReturn(true);

        boolean result = studentService.exist(1L);

        assertThat(result).isTrue();
        verify(studentRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Exist - debe retornar false si no existe")
    void testExistReturnsFalse() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        boolean result = studentService.exist(999L);

        assertThat(result).isFalse();
        verify(studentRepository, times(1)).existsById(999L);
    }

    // ==================== TESTS DE MAPEO ====================

    @Test
    @DisplayName("Mapeo - ProfileResponse debe contener nombre completo combinado")
    void testProfileResponseCombinesNames() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentProfileResponseDTO result = studentService.findProfileById(1L);

        assertThat(result.getFullName()).isEqualTo("Carlos García López");
    }

    @Test
    @DisplayName("Mapeo - FullResponse debe contener nombres separados")
    void testFullResponseContainsSeparateNames() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentFullResponseDTO result = studentService.findFullDetailById(1L);

        assertThat(result.getFirstName()).isEqualTo("Carlos");
        assertThat(result.getMiddleName()).isEqualTo("Antonio");
        assertThat(result.getLastName()).isEqualTo("García López");
    }

    @Test
    @DisplayName("Mapeo - ShortResponse debe contener nombre combinado")
    void testShortResponseCombinesNames() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student));

        List<StudentShortResponseDTO> result = studentService.findAllForTable();

        assertThat(result.get(0).getFullName()).contains("Carlos");
        assertThat(result.get(0).getFullName()).contains("García López");
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    @DisplayName("Validación - debe validar formato de RUT en duplicados")
    void testValidatesRutFormat() {
        when(studentRepository.existsByRut("22.126.386-3")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentProfileResponseDTO result = studentService.create(studentRequest);

        assertThat(result.getRut()).isEqualTo("22.126.386-3");
    }

    @Test
    @DisplayName("Validación - debe preservar datos de alergias")
    void testPreservesAllergiesData() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentProfileResponseDTO result = studentService.findProfileById(1L);

        assertThat(result.getAllergies()).isNotEmpty();
        assertThat(result.getAllergies()).hasSize(2);
    }
}
