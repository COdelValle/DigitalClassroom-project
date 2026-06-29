package cl.digitalclassroom.classroommanager.service;

import cl.digitalclassroom.classroommanager.exception.BadRequestException;
import cl.digitalclassroom.classroommanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.classroommanager.model.dto.request.CourseRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.CourseModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.entity.Classroom;
import cl.digitalclassroom.classroommanager.model.entity.Course;
import cl.digitalclassroom.classroommanager.model.entity.Subject;
import cl.digitalclassroom.classroommanager.repository.ClassroomRepository;
import cl.digitalclassroom.classroommanager.repository.CourseRepository;
import cl.digitalclassroom.classroommanager.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseServiceRepeatedTest {

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
    private Course course;
    private CourseRequestDTO createRequest;

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

    @RepeatedTest(46)
    void repeatedCreateAndUpdateCourseWorkflow() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(courseRepository.existsBySubjectIdAndClassroomIdAndSchoolYearAndSemester(1L, 1L, 2026, "1")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        var result = courseService.createCourse(createRequest);
        assertThat(result).isNotNull();
        assertThat(result.getTeacherName()).isEqualTo("Profe Test");

        CourseModifyRequestDTO modifyRequest = new CourseModifyRequestDTO();
        modifyRequest.setTeacherName("Docente Actualizado");
        modifyRequest.setSemester("2");

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        var updated = courseService.updateCourse(10L, modifyRequest);
        assertThat(updated.getSemester()).isEqualTo("2");
    }

    @RepeatedTest(1)
    void repeatedCreateCourseThrowsWhenDuplicate() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(courseRepository.existsBySubjectIdAndClassroomIdAndSchoolYearAndSemester(1L, 1L, 2026, "1")).thenReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(createRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe un curso configurado");
    }

    @RepeatedTest(1)
    void repeatedDeleteCourseDelegatesToRepository() {
        courseService.deleteCourse(10L);
        verify(courseRepository).deleteById(10L);
    }
}
