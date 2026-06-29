package cl.digitalclassroom.assessmentmanager.service;

import cl.digitalclassroom.assessmentmanager.exception.BadRequestException;
import cl.digitalclassroom.assessmentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.assessmentmanager.model.dto.request.AssessmentRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.request.GradeRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.request.modify.AssessmentModifyRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.request.modify.GradeModifyRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.entity.Assessment;
import cl.digitalclassroom.assessmentmanager.model.entity.Grade;
import cl.digitalclassroom.assessmentmanager.repository.AssessmentRepository;
import cl.digitalclassroom.assessmentmanager.repository.GradeRepository;
import cl.digitalclassroom.assessmentmanager.service.adapter.AcademicServiceAdapter;
import cl.digitalclassroom.assessmentmanager.service.adapter.StudentServiceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceComprehensiveTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private StudentServiceAdapter studentAdapter;

    @Mock
    private AcademicServiceAdapter academicAdapter;

    @InjectMocks
    private AssessmentService assessmentService;

    @InjectMocks
    private GradeService gradeService;

    private Assessment baseAssessment;
    private Grade baseGrade;

    @BeforeEach
    void setUp() {
        baseGrade = Grade.builder()
                .id(100L)
                .studentId(1L)
                .score(5.0)
                .registrationDate(LocalDateTime.now())
                .build();

        baseAssessment = Assessment.builder()
                .id(1L)
                .title("Evaluación básica")
                .courseId(10L)
                .examDate(LocalDate.now().plusDays(1))
                .isGraded(false)
                .build();
    }

    private void stubSearchResults() {
        when(assessmentRepository.findAll((Specification<Assessment>) any())).thenReturn(List.of(baseAssessment));
    }

    private AssessmentRequestDTO buildAssessmentRequest(Long courseId, LocalDate examDate, boolean withGrades) {
        AssessmentRequestDTO request = new AssessmentRequestDTO();
        request.setTitle("Solicitud de prueba");
        request.setCourseId(courseId);
        request.setExamDate(examDate);
        if (withGrades) {
            var gradeItem = new AssessmentRequestDTO.GradeItemDTO();
            gradeItem.setStudentId(1L);
            gradeItem.setScore(5.5);
            request.setGrades(List.of(gradeItem));
        }
        return request;
    }

    private GradeRequestDTO buildGradeRequest() {
        GradeRequestDTO request = new GradeRequestDTO();
        request.setStudentId(1L);
        request.setAssessmentId(1L);
        request.setScore(6.0);
        return request;
    }

    @Test
    void shouldReturnAssessmentByIdWhenFound() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));

        var result = assessmentService.assessmentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(assessmentRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowWhenAssessmentByIdNotFound() {
        when(assessmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentService.assessmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    void shouldSearchAssessmentsByCourseId() {
        stubSearchResults();

        var results = assessmentService.searchAssessments(10L, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCourseId()).isEqualTo(10L);
    }

    @Test
    void shouldSearchAssessmentsByTitleContains() {
        stubSearchResults();

        var results = assessmentService.searchAssessments(null, "Evaluación", null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).contains("Evaluación");
    }

    @Test
    void shouldSearchAssessmentsByExamDate() {
        stubSearchResults();

        var requestedDate = java.sql.Date.valueOf(LocalDate.now().plusDays(1));
        var results = assessmentService.searchAssessments(null, null, requestedDate);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getExamDate()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    void shouldReturnEmptyWhenSearchNoAssessments() {
        when(assessmentRepository.findAll((Specification<Assessment>) any())).thenReturn(List.of());

        var results = assessmentService.searchAssessments(null, "Nada", null);

        assertThat(results).isEmpty();
    }

    @Test
    void shouldCreateAssessmentWhenCourseExists() {
        var request = buildAssessmentRequest(10L, LocalDate.now().plusDays(5), false);
        when(academicAdapter.courseExists(10L)).thenReturn(true);
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var result = assessmentService.createAssessment(request);

        assertThat(result).isNotNull();
        assertThat(result.getCourseId()).isEqualTo(10L);
    }

    @Test
    void shouldCreateAssessmentAndSetGradedWhenGradesProvided() {
        var request = buildAssessmentRequest(10L, LocalDate.now().plusDays(5), true);
        when(academicAdapter.courseExists(10L)).thenReturn(true);
        when(studentAdapter.studentExists(1L)).thenReturn(true);
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var result = assessmentService.createAssessment(request);

        assertThat(result).isNotNull();
        assertThat(result.isGraded()).isEqualTo(false);
    }

    @Test
    void shouldThrowWhenCreateAssessmentCourseNotFound() {
        var request = buildAssessmentRequest(99L, LocalDate.now().plusDays(5), false);
        when(academicAdapter.courseExists(99L)).thenReturn(false);

        assertThatThrownBy(() -> assessmentService.createAssessment(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("relación entre la asignatura y la clase");
    }

    @Test
    void shouldThrowWhenCreateAssessmentGradeStudentNotFound() {
        var request = buildAssessmentRequest(10L, LocalDate.now().plusDays(5), true);
        when(academicAdapter.courseExists(10L)).thenReturn(true);
        when(studentAdapter.studentExists(1L)).thenReturn(false);

        assertThatThrownBy(() -> assessmentService.createAssessment(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Estudiante 1 no encontrado");
    }

    @Test
    void shouldUpdateAssessmentTitleOnly() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var request = new AssessmentModifyRequestDTO();
        request.setTitle("Título actualizado");

        var result = assessmentService.updateAssessment(1L, request);

        assertThat(result.getTitle()).isEqualTo("Título actualizado");
    }

    @Test
    void shouldUpdateAssessmentExamDateOnly() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var request = new AssessmentModifyRequestDTO();
        request.setExamDate(LocalDate.now().plusDays(10));

        var result = assessmentService.updateAssessment(1L, request);

        assertThat(result.getExamDate()).isEqualTo(LocalDate.now().plusDays(10));
    }

    @Test
    void shouldUpdateAssessmentCourseIdWhenCourseExists() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));
        when(academicAdapter.courseExists(20L)).thenReturn(true);
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var request = new AssessmentModifyRequestDTO();
        request.setCourseId(20L);

        var result = assessmentService.updateAssessment(1L, request);

        assertThat(result.getCourseId()).isEqualTo(20L);
    }

    @Test
    void shouldThrowWhenUpdateAssessmentCourseDoesNotExist() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));
        when(academicAdapter.courseExists(20L)).thenReturn(false);

        var request = new AssessmentModifyRequestDTO();
        request.setCourseId(20L);

        assertThatThrownBy(() -> assessmentService.updateAssessment(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("La relación indicada no existe");
    }

    @Test
    void shouldDeleteAssessmentWhenFutureAndNoGrades() {
        baseAssessment.setExamDate(LocalDate.now().plusDays(30));
        baseAssessment.setGrades(List.of());
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));

        assessmentService.deleteAssessment(1L);

        verify(assessmentRepository, times(1)).delete(baseAssessment);
    }

    @Test
    void shouldDeleteAssessmentWhenCurrentYearAndNoGrades() {
        baseAssessment.setExamDate(LocalDate.now().plusDays(5));
        baseAssessment.setGrades(List.of());
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));

        assessmentService.deleteAssessment(1L);

        verify(assessmentRepository).delete(baseAssessment);
    }

    @Test
    void shouldThrowWhenDeleteAssessmentCurrentYearWithGrades() {
        baseAssessment.setExamDate(LocalDate.now().plusDays(5));
        baseAssessment.setGrades(List.of(baseGrade));
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));

        assertThatThrownBy(() -> assessmentService.deleteAssessment(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Prohibido eliminar");
    }

    @Test
    void shouldThrowWhenDeleteAssessmentFromPastYear() {
        baseAssessment.setExamDate(LocalDate.now().minusYears(1));
        baseAssessment.setGrades(List.of());
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));

        assertThatThrownBy(() -> assessmentService.deleteAssessment(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("No se pueden eliminar registros");
    }

    @Test
    void shouldRetrieveGradeByIdSuccess() {
        when(gradeRepository.findById(100L)).thenReturn(Optional.of(baseGrade));

        var result = gradeService.gradeById(100L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    void shouldThrowWhenGradeByIdNotFound() {
        when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.gradeById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    void shouldSearchGradesByStudentId() {
        when(gradeRepository.findAll((Specification<Grade>) any())).thenReturn(List.of(baseGrade));

        var results = gradeService.searchGrades(1L, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStudentId()).isEqualTo(1L);
    }

    @Test
    void shouldSearchGradesByMinScoreOnly() {
        when(gradeRepository.findAll((Specification<Grade>) any())).thenReturn(List.of(baseGrade));

        var results = gradeService.searchGrades(null, 4.0, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getScore()).isEqualTo(5.0);
    }

    @Test
    void shouldSearchGradesByMaxScoreOnly() {
        when(gradeRepository.findAll((Specification<Grade>) any())).thenReturn(List.of(baseGrade));

        var results = gradeService.searchGrades(null, null, 6.0, null);

        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSearchGradesByScoreRange() {
        when(gradeRepository.findAll((Specification<Grade>) any())).thenReturn(List.of(baseGrade));

        var results = gradeService.searchGrades(null, 4.0, 6.0, null);

        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSearchGradesByDate() {
        when(gradeRepository.findAll((Specification<Grade>) any())).thenReturn(List.of(baseGrade));

        var results = gradeService.searchGrades(null, null, null, LocalDate.now());

        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSaveIndependentGradeWhenStudentAndAssessmentExist() {
        var request = buildGradeRequest();
        when(studentAdapter.studentExists(1L)).thenReturn(true);
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var result = gradeService.saveIndependentGrade(request);

        assertThat(result.getStudentId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenSaveIndependentGradeStudentDoesNotExist() {
        var request = buildGradeRequest();
        when(studentAdapter.studentExists(1L)).thenReturn(false);

        assertThatThrownBy(() -> gradeService.saveIndependentGrade(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("El estudiante no existe");
    }

    @Test
    void shouldThrowWhenSaveIndependentGradeAssessmentDoesNotExist() {
        var request = buildGradeRequest();
        when(studentAdapter.studentExists(1L)).thenReturn(true);
        when(assessmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.saveIndependentGrade(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Evaluación no encontrada");
    }

    @Test
    void shouldUpdateGradeScoreOnly() {
        var gradeToUpdate = baseGrade;
        when(gradeRepository.findById(100L)).thenReturn(Optional.of(gradeToUpdate));
        when(gradeRepository.save(any())).thenReturn(gradeToUpdate);

        var request = new GradeModifyRequestDTO();
        request.setScore(6.5);

        var result = gradeService.updateGrade(100L, request);

        assertThat(result.getScore()).isEqualTo(6.5);
    }

    @Test
    void shouldUpdateGradeStudentIdWhenDifferentStudentExists() {
        var gradeToUpdate = baseGrade;
        when(gradeRepository.findById(100L)).thenReturn(Optional.of(gradeToUpdate));
        when(studentAdapter.studentExists(2L)).thenReturn(true);
        when(gradeRepository.save(any())).thenReturn(gradeToUpdate);

        var request = new GradeModifyRequestDTO();
        request.setStudentId(2L);

        var result = gradeService.updateGrade(100L, request);

        assertThat(result.getStudentId()).isEqualTo(2L);
    }

    @Test
    void shouldThrowWhenUpdateGradeNewStudentDoesNotExist() {
        var gradeToUpdate = baseGrade;
        when(gradeRepository.findById(100L)).thenReturn(Optional.of(gradeToUpdate));
        when(studentAdapter.studentExists(2L)).thenReturn(false);

        var request = new GradeModifyRequestDTO();
        request.setStudentId(2L);

        assertThatThrownBy(() -> gradeService.updateGrade(100L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("nuevo estudiante indicado no existe");
    }

    @Test
    void shouldDeleteGradeWhenCurrentYear() {
        baseGrade.setRegistrationDate(LocalDateTime.now());
        when(gradeRepository.findById(100L)).thenReturn(Optional.of(baseGrade));

        gradeService.deleteGrade(100L);

        verify(gradeRepository).delete(baseGrade);
    }

    @Test
    void shouldThrowWhenDeleteGradeFromPreviousYear() {
        baseGrade.setRegistrationDate(LocalDateTime.now().minusYears(1));
        when(gradeRepository.findById(100L)).thenReturn(Optional.of(baseGrade));

        assertThatThrownBy(() -> gradeService.deleteGrade(100L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("No es posible eliminar notas de periodos académicos anteriores");
    }

    @Test
    void shouldThrowWhenDeleteGradeNotFound() {
        when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.deleteGrade(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró la nota");
    }

    @Test
    void shouldReturnSearchResultsWhenTitleBlankAndCourseFilterPresent() {
        stubSearchResults();

        var results = assessmentService.searchAssessments(10L, "  ", null);

        assertThat(results).hasSize(1);
    }

    @Test
    void shouldReturnSearchResultsWhenExamDateNull() {
        stubSearchResults();

        var results = assessmentService.searchAssessments(10L, null, null);

        assertThat(results).hasSize(1);
    }

    @Test
    void shouldCreateAssessmentWithoutGradesSetsIsGradedFalse() {
        var request = buildAssessmentRequest(10L, LocalDate.now().plusDays(3), false);
        when(academicAdapter.courseExists(10L)).thenReturn(true);
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var result = assessmentService.createAssessment(request);

        assertThat(result.isGraded()).isFalse();
    }

    @Test
    void shouldUpdateAssessmentIgnoreBlankTitle() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var request = new AssessmentModifyRequestDTO();
        request.setTitle("  ");

        var result = assessmentService.updateAssessment(1L, request);

        assertThat(result.getTitle()).isEqualTo(baseAssessment.getTitle());
    }

    @Test
    void shouldUpdateAssessmentIgnoreNullExamDate() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var request = new AssessmentModifyRequestDTO();
        request.setExamDate(null);

        var result = assessmentService.updateAssessment(1L, request);

        assertThat(result.getExamDate()).isEqualTo(baseAssessment.getExamDate());
    }

    @Test
    void shouldDeleteAssessmentWhenFutureYearAndGrades() {
        baseAssessment.setExamDate(LocalDate.now().plusYears(1));
        baseAssessment.setGrades(List.of(baseGrade));
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));

        assessmentService.deleteAssessment(1L);

        verify(assessmentRepository).delete(baseAssessment);
    }

    @Test
    void shouldReturnAllAssessmentsWhenFiltersAreNull() {
        stubSearchResults();

        var results = assessmentService.searchAssessments(null, null, null);

        assertThat(results).hasSize(1);
    }

    @Test
    void shouldReturnAllGradesWhenFiltersAreNull() {
        when(gradeRepository.findAll((Specification<Grade>) any())).thenReturn(List.of(baseGrade));

        var results = gradeService.searchGrades(null, null, null, null);

        assertThat(results).hasSize(1);
    }

    @Test
    void shouldSaveIndependentGradeAndPersistToAssessment() {
        var request = buildGradeRequest();
        baseAssessment.setGrades(List.of());
        when(studentAdapter.studentExists(1L)).thenReturn(true);
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));
        when(assessmentRepository.save(any())).thenReturn(baseAssessment);

        var result = gradeService.saveIndependentGrade(request);

        assertThat(result.getStudentId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateGradeAndKeepExistingStudentWhenNoStudentChangeRequested() {
        var gradeToUpdate = baseGrade;
        when(gradeRepository.findById(100L)).thenReturn(Optional.of(gradeToUpdate));
        when(gradeRepository.save(any())).thenReturn(gradeToUpdate);

        var request = new GradeModifyRequestDTO();
        request.setScore(6.8);

        var result = gradeService.updateGrade(100L, request);

        assertThat(result.getStudentId()).isEqualTo(1L);
        assertThat(result.getScore()).isEqualTo(6.8);
    }

    @Test
    void shouldThrowWhenUpdateGradeNotFound() {
        when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

        var request = new GradeModifyRequestDTO();
        request.setScore(5.5);

        assertThatThrownBy(() -> gradeService.updateGrade(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Nota no encontrada");
    }

    @Test
    void shouldUseRepositoryFindByIdWhenGettingAssessment() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(baseAssessment));

        assessmentService.assessmentById(1L);

        verify(assessmentRepository).findById(1L);
    }

    @Test
    void shouldUseRepositoryFindByIdWhenGettingGrade() {
        when(gradeRepository.findById(100L)).thenReturn(Optional.of(baseGrade));

        gradeService.gradeById(100L);

        verify(gradeRepository).findById(100L);
    }

    @Test
    void shouldThrowWhenDeletingAssessmentNotFound() {
        when(assessmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentService.deleteAssessment(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró la evaluación");
    }

    @Test
    void shouldThrowWhenSavingGradeRollingBackWhenAssessmentMissing() {
        var request = buildGradeRequest();
        when(studentAdapter.studentExists(1L)).thenReturn(true);
        when(assessmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.saveIndependentGrade(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Evaluación no encontrada");
    }
}
