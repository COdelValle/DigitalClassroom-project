package cl.digitalclassroom.studentmanager.service;

import cl.digitalclassroom.studentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.studentmanager.exception.StudentAlreadyExistsException;
import cl.digitalclassroom.studentmanager.mapper.StudentMapper;
import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentFullResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentProfileResponseDTO;
import cl.digitalclassroom.studentmanager.model.entity.Student;
import cl.digitalclassroom.studentmanager.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para StudentService.
 * Prueba la lógica de negocio sin acceder a la base de datos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService - Tests Unitarios")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private StudentRequestDTO requestDTO;
    private Student student;
    private StudentProfileResponseDTO profileDTO;

    /**
     * Prepara datos de prueba antes de cada test.
     */
    @BeforeEach
    void setUp() {
        // Crear DTO de solicitud
        requestDTO = StudentRequestDTO.builder()
                .rut("12.345.678-9")
                .firstName("Juan")
                .middleName("Carlos")
                .lastName("García López")
                .birthDate(new Date(694224000000L)) // Una fecha en el pasado
                .allergies(Arrays.asList("Maní", "Camarones"))
                .legalRepresentatives(Arrays.asList(
                        LegalRepresentativeDTO.builder()
                                .rut("11.111.111-1")
                                .fullName("María García")
                                .email("maria@email.com")
                                .phoneNumber(Arrays.asList("+56912345678"))
                                .relationship("Madre")
                                .build()
                ))
                .build();

        // Crear entidad Student
        student = Student.builder()
                .id(1L)
                .rut("12.345.678-9")
                .firstName("Juan")
                .middleName("Carlos")
                .lastName("García López")
                .birthDate(new Date(694224000000L))
                .allergies(Arrays.asList("Maní", "Camarones"))
                .build();

        // Crear DTO de respuesta
        profileDTO = StudentProfileResponseDTO.builder()
                .id(1L)
                .rut("12.345.678-9")
                .fullName("Juan García López")
                .allergies(Arrays.asList("Maní", "Camarones"))
                .build();
    }

    /**
     * Test: Crear un nuevo estudiante exitosamente.
     */
    @Test
    @DisplayName("Debe crear un nuevo estudiante correctamente")
    void testCreateStudentSuccess() {
        // Arrange: Configura el comportamiento de los mocks
        when(studentRepository.existsByRut(requestDTO.getRut())).thenReturn(false);
        when(studentMapper.requestDtoToEntity(requestDTO)).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.entityToProfileResponseDto(student)).thenReturn(profileDTO);

        // Act: Ejecuta el método a probar
        StudentProfileResponseDTO result = studentService.create(requestDTO);

        // Assert: Verifica el resultado
        assertNotNull(result);
        assertEquals("Juan García López", result.getFullName());
        assertEquals("12.345.678-9", result.getRut());

        // Verifica que se llamaron los métodos esperados
        verify(studentRepository).existsByRut(requestDTO.getRut());
        verify(studentRepository).save(any(Student.class));
    }

    /**
     * Test: Intenta crear un estudiante con RUT duplicado.
     */
    @Test
    @DisplayName("Debe lanzar excepción al crear estudiante con RUT duplicado")
    void testCreateStudentWithDuplicateRut() {
        // Arrange
        when(studentRepository.existsByRut(requestDTO.getRut())).thenReturn(true);

        // Act & Assert
        assertThrows(StudentAlreadyExistsException.class, () -> {
            studentService.create(requestDTO);
        });

        // Verifica que NO se guardó nada
        verify(studentRepository, never()).save(any(Student.class));
    }

    /**
     * Test: Buscar estudiante por ID exitosamente.
     */
    @Test
    @DisplayName("Debe encontrar un estudiante por ID")
    void testFindProfileByIdSuccess() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.entityToProfileResponseDto(student)).thenReturn(profileDTO);

        // Act
        StudentProfileResponseDTO result = studentService.findProfileById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan García López", result.getFullName());
    }

    /**
     * Test: Intenta buscar estudiante que no existe.
     */
    @Test
    @DisplayName("Debe lanzar excepción cuando no encuentra estudiante por ID")
    void testFindProfileByIdNotFound() {
        // Arrange
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.findProfileById(999L);
        });
    }

    /**
     * Test: Listar estudiantes con paginación.
     */
    @Test
    @DisplayName("Debe devolver página de estudiantes")
    void testFindAllForTableWithPagination() {
        // Arrange
        Page<Student> page = new PageImpl<>(List.of(student));
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(studentMapper.entityToShortResponseDto(any(Student.class)))
                .thenReturn(null); // Simplificado para el test

        // Act
        Page<?> result = studentService.findAllForTable(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    /**
     * Test: Eliminar estudiante exitosamente.
     */
    @Test
    @DisplayName("Debe eliminar un estudiante correctamente")
    void testDeleteStudentSuccess() {
        // Arrange
        when(studentRepository.existsById(1L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> studentService.delete(1L));

        // Assert
        verify(studentRepository).deleteById(1L);
    }

    /**
     * Test: Intenta eliminar estudiante que no existe.
     */
    @Test
    @DisplayName("Debe lanzar excepción al eliminar estudiante inexistente")
    void testDeleteStudentNotFound() {
        // Arrange
        when(studentRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.delete(999L);
        });

        // Verifica que NO se intentó eliminar
        verify(studentRepository, never()).deleteById(anyLong());
    }

    /**
     * Test: Obtener conteo total de estudiantes.
     */
    @Test
    @DisplayName("Debe obtener el conteo total de estudiantes")
    void testCountTotal() {
        // Arrange
        when(studentRepository.count()).thenReturn(5L);

        // Act
        long result = studentService.countTotal();

        // Assert
        assertEquals(5L, result);
        verify(studentRepository).count();
    }
}

