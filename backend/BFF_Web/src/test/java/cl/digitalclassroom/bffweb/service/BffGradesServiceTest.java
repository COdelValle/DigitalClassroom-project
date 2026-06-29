package cl.digitalclassroom.bffweb.service;

import cl.digitalclassroom.bffweb.client.AssessmentFeignClient;
import cl.digitalclassroom.bffweb.client.ClassroomFeignClient;
import cl.digitalclassroom.bffweb.client.StudentFeignClient;
import cl.digitalclassroom.bffweb.dto.external.ClassroomResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.CourseResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.GradeResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.StudentProfileResponseDTO;
import cl.digitalclassroom.bffweb.dto.request.GradeRequestDTO;
import cl.digitalclassroom.bffweb.dto.response.BffGradeSearchResponseDTO;
import cl.digitalclassroom.bffweb.dto.response.StudentReportCardDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BffGradesServiceTest {

    @Mock
    private StudentFeignClient studentClient;

    @Mock
    private ClassroomFeignClient classroomClient;

    @Mock
    private AssessmentFeignClient assessmentClient;

    @InjectMocks
    private BffGradesService bffGradesService;

    private StudentProfileResponseDTO studentProfile;
    private ClassroomResponseDTO classroomResponse;
    private CourseResponseDTO courseResponse;
    private GradeResponseDTO gradeResponse;

    @BeforeEach
    void setUp() {
        studentProfile = StudentProfileResponseDTO.builder()
                .id(1L)
                .rut("22.222.222-2")
                .fullName("Ana Pérez")
                .build();

        classroomResponse = ClassroomResponseDTO.builder()
                .id(10L)
                .name("Aula 101")
                .studentIds(List.of(1L))
                .build();

        courseResponse = CourseResponseDTO.builder()
                .id(100L)
                .subject(null)
                .teacherName("Profe Test")
                .build();

        gradeResponse = GradeResponseDTO.builder()
                .id(1000L)
                .studentId(1L)
                .score(5.5)
                .registrationDate(LocalDateTime.now())
                .build();
    }

    @Test
    void getStudentReportCardReturnsReportForStudentInClassroom() {
        when(studentClient.getStudentById(1L)).thenReturn(studentProfile);
        when(classroomClient.searchClassrooms(any(), eq(LocalDate.now().getYear())))
                .thenReturn(List.of(classroomResponse));
        when(assessmentClient.searchGrades(1L, null, null, null)).thenReturn(List.of(gradeResponse));
        when(classroomClient.searchCourses(10L, null, null)).thenReturn(List.of(courseResponse));

        StudentReportCardDTO reportCard = bffGradesService.getStudentReportCard(1L);

        assertThat(reportCard).isNotNull();
        assertThat(reportCard.getStudentName()).isEqualTo("Ana Pérez");
        assertThat(reportCard.getRut()).isEqualTo("22.222.222-2");
        assertThat(reportCard.getClassroomName()).isEqualTo("Aula 101");
        assertThat(reportCard.getFinalAverage()).isEqualTo(5.5);
        assertThat(reportCard.getSubjects()).hasSize(1);
    }

    @Test
    void searchBffGradesReturnsEnrichedResults() {
        when(assessmentClient.searchGrades(1L, 4.0, 6.0, "2026-06-01"))
                .thenReturn(List.of(gradeResponse));
        when(studentClient.getStudentById(1L)).thenReturn(studentProfile);
        when(classroomClient.searchClassrooms(any(), any())).thenReturn(List.of(classroomResponse));

        List<BffGradeSearchResponseDTO> results = bffGradesService.searchBffGrades(1L, 4.0, 6.0, "2026-06-01");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getGradeValue()).isEqualTo(5.5);
        assertThat(results.get(0).getStudentName()).isEqualTo("Ana Pérez");
    }

    @Test
    void addGradeCallsAssessmentClientWhenStudentExists() {
        GradeRequestDTO request = new GradeRequestDTO();
        request.setStudentId(1L);
        request.setAssessmentId(2L);
        request.setScore(6.0);

        when(studentClient.getStudentById(1L)).thenReturn(studentProfile);
        when(assessmentClient.saveGrade(request)).thenReturn(gradeResponse);

        GradeResponseDTO response = bffGradesService.addGrade(request);

        assertThat(response).isEqualTo(gradeResponse);
    }
}
