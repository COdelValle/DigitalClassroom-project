package cl.digitalclassroom.classroommanager.service;

import cl.digitalclassroom.classroommanager.exception.BadRequestException;
import cl.digitalclassroom.classroommanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.classroommanager.model.dto.request.CourseRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.CourseModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.response.CourseResponseDTO;
import cl.digitalclassroom.classroommanager.model.entity.Classroom;
import cl.digitalclassroom.classroommanager.model.entity.Course;
import cl.digitalclassroom.classroommanager.model.entity.Subject;
import cl.digitalclassroom.classroommanager.repository.ClassroomRepository;
import cl.digitalclassroom.classroommanager.repository.CourseRepository;
import cl.digitalclassroom.classroommanager.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private ClassroomRepository classroomRepository;

    @InjectMocks
    private CourseService courseService;

    private Subject subject;
    private Classroom classroom;
    private CourseRequestDTO createRequest;
    private Course course;

    @BeforeEach
    void setUp() {
        subject = Subject.builder().id(1L).name("Matemáticas").build();
        classroom = Classroom.builder().id(1L).name("Aula A").build();

        createRequest = new CourseRequestDTO();
        createRequest.setSubjectId(1L);
        createRequest.setClassroomId(1L);
        createRequest.setSchoolYear(2026);
        createRequest.setSemester("1");
        createRequest.setTeacherName("Profe Test");

        course = Course.builder()
                .id(10L)
                .subject(subject)
                .classroom(classroom)
                .schoolYear(2026)
                .semester("1")
                .teacherName("Profe Test")
                .build();
    }

    @Test
    void createCourseReturnsSavedCourse() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(courseRepository.existsBySubjectIdAndClassroomIdAndSchoolYearAndSemester(
                1L, 1L, 2026, "1")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseResponseDTO result = courseService.createCourse(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTeacherName()).isEqualTo("Profe Test");
    }

    @Test
    void createCourseThrowsWhenDuplicateExists() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(courseRepository.existsBySubjectIdAndClassroomIdAndSchoolYearAndSemester(
                1L, 1L, 2026, "1")).thenReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(createRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe un curso configurado");
    }

    @Test
    void existReturnsTrueWhenCourseExists() {
        when(courseRepository.existsById(10L)).thenReturn(true);

        boolean exists = courseService.exist(10L);

        assertThat(exists).isTrue();
    }

    @Test
    void updateCourseThrowsWhenNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        CourseModifyRequestDTO request = new CourseModifyRequestDTO();
        request.setTeacherName("Docente");
        request.setSemester("2");

        assertThatThrownBy(() -> courseService.updateCourse(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Curso no encontrado");
    }
}
