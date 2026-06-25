package cl.digitalclassroom.studentmanager.repository;

import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import cl.digitalclassroom.studentmanager.model.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas para StudentRepository
 * Utiliza @DataJpaTest para cargar solo la capa JPA
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("StudentRepository Tests")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private Student testStudent;
    private LegalRepresentativeDTO representative;

    @BeforeEach
    void setUp() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = sdf.parse("2005-05-15");

        representative = LegalRepresentativeDTO.builder()
                .rut("11.920.072-5")
                .fullName("Juan Pérez")
                .email("juan.p@example.com")
                .phoneNumber(Arrays.asList("+56 9 1111 2222"))
                .relationship("Padre")
                .build();

        testStudent = Student.builder()
                .rut("22.126.386-3")
                .firstName("Carlos")
                .middleName("Antonio")
                .lastName("García López")
                .birthDate(birthDate)
                .allergies(Arrays.asList("Polen", "Maíz"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();
    }

    // ==================== TESTS DE SAVE ====================

    @Test
    @DisplayName("Save - debe guardar estudiante correctamente")
    void testSaveStudentSuccess() {
        Student saved = studentRepository.save(testStudent);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRut()).isEqualTo("22.126.386-3");
    }

    @Test
    @DisplayName("Save - debe asignar ID auto-generado")
    void testSaveAssignsId() {
        assertThat(testStudent.getId()).isNull();

        Student saved = studentRepository.save(testStudent);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Save - debe preservar todos los datos")
    void testSavePreservesAllData() {
        Student saved = studentRepository.save(testStudent);

        assertThat(saved.getRut()).isEqualTo("22.126.386-3");
        assertThat(saved.getFirstName()).isEqualTo("Carlos");
        assertThat(saved.getMiddleName()).isEqualTo("Antonio");
        assertThat(saved.getLastName()).isEqualTo("García López");
        assertThat(saved.getAllergies()).contains("Polen", "Maíz");
    }

    // ==================== TESTS DE FIND BY ID ====================

    @Test
    @DisplayName("FindById - debe retornar estudiante existente")
    void testFindByIdSuccess() {
        Student saved = studentRepository.save(testStudent);

        Optional<Student> found = studentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getRut()).isEqualTo("22.126.386-3");
    }

    @Test
    @DisplayName("FindById - debe retornar vacío si no existe")
    void testFindByIdNotFound() {
        Optional<Student> found = studentRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("FindById - debe retornar con datos completos")
    void testFindByIdReturnsCompleteData() {
        Student saved = studentRepository.save(testStudent);

        Optional<Student> found = studentRepository.findById(saved.getId());

        assertThat(found.get().getFirstName()).isEqualTo("Carlos");
        assertThat(found.get().getAllergies()).isNotEmpty();
        assertThat(found.get().getLegalRepresentatives()).isNotEmpty();
    }

    // ==================== TESTS DE FIND ALL ====================

    @Test
    @DisplayName("FindAll - debe retornar todos los estudiantes")
    void testFindAllSuccess() {
        studentRepository.save(testStudent);

        Student student2 = Student.builder()
                .rut("18.456.789-0")
                .firstName("María")
                .lastName("Rodríguez")
                .birthDate(testStudent.getBirthDate())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();

        studentRepository.save(student2);

        List<Student> all = studentRepository.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("FindAll - debe retornar lista vacía si no hay datos")
    void testFindAllEmpty() {
        List<Student> all = studentRepository.findAll();

        assertThat(all).isEmpty();
    }

    @Test
    @DisplayName("FindAll - debe retornar datos con ID asignado")
    void testFindAllReturnsWithIds() {
        studentRepository.save(testStudent);

        List<Student> all = studentRepository.findAll();

        assertThat(all).allMatch(s -> s.getId() != null);
    }

    // ==================== TESTS DE EXISTS BY ID ====================

    @Test
    @DisplayName("ExistsById - debe retornar true si existe")
    void testExistsByIdTrue() {
        Student saved = studentRepository.save(testStudent);

        boolean exists = studentRepository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("ExistsById - debe retornar false si no existe")
    void testExistsByIdFalse() {
        boolean exists = studentRepository.existsById(999L);

        assertThat(exists).isFalse();
    }

    // ==================== TESTS DE EXISTS BY RUT ====================

    @Test
    @DisplayName("ExistsByRut - debe retornar true si RUT existe")
    void testExistsByRutTrue() {
        studentRepository.save(testStudent);

        boolean exists = studentRepository.existsByRut("22.126.386-3");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("ExistsByRut - debe retornar false si RUT no existe")
    void testExistsByRutFalse() {
        boolean exists = studentRepository.existsByRut("99.999.999-9");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("ExistsByRut - debe ser case sensitive")
    void testExistsByRutCaseSensitive() {
        studentRepository.save(testStudent);

        boolean exists = studentRepository.existsByRut("22.126.386-3");

        assertThat(exists).isTrue();
    }

    // ==================== TESTS DE UPDATE ====================

    @Test
    @DisplayName("Update - debe actualizar estudiante existente")
    void testUpdateSuccess() {
        Student saved = studentRepository.save(testStudent);

        Student toUpdate = saved;
        toUpdate.setFirstName("Updated Name");

        Student updated = studentRepository.save(toUpdate);

        assertThat(updated.getFirstName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Update - debe modificar en base de datos")
    void testUpdateModifiesInDatabase() {
        Student saved = studentRepository.save(testStudent);
        Long id = saved.getId();

        saved.setLastName("Updated Last Name");
        studentRepository.save(saved);

        Optional<Student> retrieved = studentRepository.findById(id);

        assertThat(retrieved.get().getLastName()).isEqualTo("Updated Last Name");
    }

    // ==================== TESTS DE DELETE ====================

    @Test
    @DisplayName("DeleteById - debe eliminar estudiante existente")
    void testDeleteByIdSuccess() {
        Student saved = studentRepository.save(testStudent);
        Long id = saved.getId();

        studentRepository.deleteById(id);

        Optional<Student> found = studentRepository.findById(id);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("DeleteById - debe reducir el conteo")
    void testDeleteByIdReducesCount() {
        studentRepository.save(testStudent);
        long countBefore = studentRepository.count();

        Student student2 = Student.builder()
                .rut("18.456.789-0")
                .firstName("Test")
                .lastName("Student")
                .birthDate(testStudent.getBirthDate())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();

        studentRepository.save(student2);

        studentRepository.deleteById(testStudent.getId());

        long countAfter = studentRepository.count();

        assertThat(countAfter).isEqualTo(1);
    }

    @Test
    @DisplayName("Delete - debe permitir eliminar múltiples")
    void testDeleteMultiple() {
        studentRepository.save(testStudent);

        Student student2 = Student.builder()
                .rut("18.456.789-0")
                .firstName("Test")
                .lastName("Student")
                .birthDate(testStudent.getBirthDate())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();

        Student saved2 = studentRepository.save(student2);

        studentRepository.deleteAll();

        assertThat(studentRepository.findAll()).isEmpty();
    }

    // ==================== TESTS DE CONSTRAINTS ====================

    @Test
    @DisplayName("Constraint - debe rechazar RUT duplicado")
    void testConstraintRejectsDuplicateRut() {
        studentRepository.save(testStudent);

        Student duplicate = Student.builder()
                .rut("22.126.386-3")  // Mismo RUT
                .firstName("Otro")
                .lastName("Otro")
                .birthDate(testStudent.getBirthDate())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();

        assertThatThrownBy(() -> studentRepository.saveAndFlush(duplicate))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Constraint - debe preservar relaciones")
    void testConstraintPreservesRelationships() {
        Student saved = studentRepository.save(testStudent);

        Optional<Student> found = studentRepository.findById(saved.getId());

        assertThat(found.get().getLegalRepresentatives()).isNotEmpty();
    }

    // ==================== TESTS DE COUNT ====================

    @Test
    @DisplayName("Count - debe retornar cero inicialmente")
    void testCountInitially() {
        long count = studentRepository.count();

        assertThat(count).isZero();
    }

    @Test
    @DisplayName("Count - debe incrementar con cada guardado")
    void testCountIncreases() {
        assertThat(studentRepository.count()).isZero();

        studentRepository.save(testStudent);
        assertThat(studentRepository.count()).isEqualTo(1);

        Student student2 = Student.builder()
                .rut("18.456.789-0")
                .firstName("Test")
                .lastName("Student")
                .birthDate(testStudent.getBirthDate())
                .allergies(Arrays.asList("Ninguna"))
                .legalRepresentatives(Arrays.asList(representative))
                .build();

        studentRepository.save(student2);
        assertThat(studentRepository.count()).isEqualTo(2);
    }
}
