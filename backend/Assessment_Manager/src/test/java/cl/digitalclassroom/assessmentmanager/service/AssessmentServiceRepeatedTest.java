package cl.digitalclassroom.assessmentmanager.service;

import cl.digitalclassroom.assessmentmanager.model.entity.Assessment;
import cl.digitalclassroom.assessmentmanager.repository.AssessmentRepository;
import cl.digitalclassroom.assessmentmanager.service.adapter.AcademicServiceAdapter;
import cl.digitalclassroom.assessmentmanager.service.adapter.StudentServiceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceRepeatedTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private StudentServiceAdapter studentAdapter;

    @Mock
    private AcademicServiceAdapter academicAdapter;

    @InjectMocks
    private AssessmentService assessmentService;

    private Assessment assessment;

    @BeforeEach
    void setUp() {
        assessment = Assessment.builder()
                .id(1L)
                .title("Examen de Ciencias")
                .courseId(10L)
                .examDate(LocalDate.now().plusDays(1))
                .isGraded(false)
                .build();
    }

    @RepeatedTest(47)
    void repeatedSearchAssessmentsReturnsResults() {
        when(assessmentRepository.findAll((Specification<Assessment>) any())).thenReturn(List.of(assessment));

        assertThat(assessmentService.searchAssessments(10L, "Ciencias", null))
                .hasSize(1)
                .first()
                .extracting("title")
                .isEqualTo("Examen de Ciencias");

        assertThat(assessmentService.searchAssessments(null, null, null)).isNotEmpty();
    }

    @RepeatedTest(1)
    void repeatedSearchAssessmentsWithNoMatchesReturnsEmpty() {
        when(assessmentRepository.findAll((Specification<Assessment>) any())).thenReturn(List.of());

        assertThat(assessmentService.searchAssessments(999L, "No Existe", null)).isEmpty();
    }

    @RepeatedTest(1)
    void repeatedCreateAssessmentValidatesCourseExists() {
        when(academicAdapter.courseExists(anyLong())).thenReturn(true);
        when(assessmentRepository.save(any())).thenReturn(assessment);

        var request = new cl.digitalclassroom.assessmentmanager.model.dto.request.AssessmentRequestDTO();
        request.setTitle("Evaluación repetida");
        request.setCourseId(10L);
        request.setExamDate(LocalDate.now().plusDays(2));

        assertThat(assessmentService.createAssessment(request)).isNotNull();
    }
}
