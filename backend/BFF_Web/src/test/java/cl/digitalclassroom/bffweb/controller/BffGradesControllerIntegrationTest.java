package cl.digitalclassroom.bffweb.controller;

import cl.digitalclassroom.bffweb.client.AssessmentFeignClient;
import cl.digitalclassroom.bffweb.client.ClassroomFeignClient;
import cl.digitalclassroom.bffweb.client.StudentFeignClient;
import cl.digitalclassroom.bffweb.dto.external.ClassroomResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.CourseResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.GradeResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.StudentProfileResponseDTO;
import cl.digitalclassroom.bffweb.dto.request.GradeRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BffGradesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentFeignClient studentClient;

    @MockBean
    private ClassroomFeignClient classroomClient;

    @MockBean
    private AssessmentFeignClient assessmentClient;

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
    void getStudentReportCard_shouldReturnStudentReportCard() throws Exception {
        when(studentClient.getStudentById(1L)).thenReturn(studentProfile);
        when(classroomClient.searchClassrooms(any(), eq(LocalDate.now().getYear())))
                .thenReturn(List.of(classroomResponse));
        when(assessmentClient.searchGrades(1L, null, null, null))
                .thenReturn(List.of(gradeResponse));
        when(classroomClient.searchCourses(10L, null, null))
                .thenReturn(List.of(courseResponse));

        mockMvc.perform(get("/api/v1/bff/grades/student/{studentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.studentName").value("Ana Pérez"))
                .andExpect(jsonPath("$.rut").value("22.222.222-2"))
                .andExpect(jsonPath("$.classroomName").value("Aula 101"))
                .andExpect(jsonPath("$.finalAverage").value(5.5))
                .andExpect(jsonPath("$.subjects[0].courseId").value(100));
    }

    @Test
    void searchBffGrades_shouldReturnEnrichedSearchResults() throws Exception {
        when(assessmentClient.searchGrades(1L, 4.0, 6.0, "2026-06-01"))
                .thenReturn(List.of(gradeResponse));
        when(studentClient.getStudentById(1L)).thenReturn(studentProfile);
        when(classroomClient.searchClassrooms(any(), any()))
                .thenReturn(List.of(classroomResponse));

        mockMvc.perform(get("/api/v1/bff/grades/search")
                        .param("studentId", "1")
                        .param("minScore", "4.0")
                        .param("maxScore", "6.0")
                        .param("date", "2026-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gradeValue").value(5.5))
                .andExpect(jsonPath("$[0].studentName").value("Ana Pérez"));
    }

    @Test
    void addGrade_shouldReturnCreated() throws Exception {
        GradeRequestDTO request = new GradeRequestDTO();
        request.setStudentId(1L);
        request.setAssessmentId(2L);
        request.setScore(6.0);

        when(studentClient.getStudentById(1L)).thenReturn(studentProfile);
        when(assessmentClient.saveGrade(request)).thenReturn(gradeResponse);

        mockMvc.perform(post("/api/v1/bff/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1000))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.score").value(5.5));
    }
}
