package cl.digitalclassroom.bffweb.service;

import cl.digitalclassroom.bffweb.client.AssessmentFeignClient;
import cl.digitalclassroom.bffweb.client.ClassroomFeignClient;
import cl.digitalclassroom.bffweb.client.StudentFeignClient;
import cl.digitalclassroom.bffweb.dto.external.ClassroomResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.CourseResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.GradeResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.StudentProfileResponseDTO;
import cl.digitalclassroom.bffweb.dto.response.BffGradeSearchResponseDTO;
import cl.digitalclassroom.bffweb.dto.response.StudentReportCardDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BffGradesServiceRepeatedTest {

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

    @RepeatedTest(46)
    void repeatedGetStudentReportCardAndSearchBffGrades() {
        when(studentClient.getStudentById(anyLong())).thenReturn(studentProfile);
        when(classroomClient.searchClassrooms(any(), anyInt())).thenReturn(List.of(classroomResponse));
        when(assessmentClient.searchGrades(anyLong(), any(), any(), any())).thenReturn(List.of(gradeResponse));
        when(classroomClient.searchCourses(eq(10L), any(), any())).thenReturn(List.of(courseResponse));

        StudentReportCardDTO reportCard = bffGradesService.getStudentReportCard(1L);
        assertThat(reportCard).isNotNull();
        assertThat(reportCard.getStudentName()).isEqualTo("Ana Pérez");
        assertThat(reportCard.getClassroomName()).isEqualTo("Aula 101");
        assertThat(reportCard.getFinalAverage()).isEqualTo(5.5);

        List<BffGradeSearchResponseDTO> results = bffGradesService.searchBffGrades(1L, 4.0, 6.0, "2026-06-01");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getGradeValue()).isEqualTo(5.5);
    }
}
