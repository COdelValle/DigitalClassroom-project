package cl.digitalclassroom.classroommanager.service;

import cl.digitalclassroom.classroommanager.exception.BadRequestException;
import cl.digitalclassroom.classroommanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.classroommanager.model.dto.request.CourseRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.CourseModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.response.CourseResponseDTO;
import cl.digitalclassroom.classroommanager.model.entity.Classroom;
import cl.digitalclassroom.classroommanager.model.entity.Course;
import cl.digitalclassroom.classroommanager.model.entity.Subject;
import cl.digitalclassroom.classroommanager.model.specifications.AcademicSpecifications;
import cl.digitalclassroom.classroommanager.repository.ClassroomRepository;
import cl.digitalclassroom.classroommanager.repository.CourseRepository;
import cl.digitalclassroom.classroommanager.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final ClassroomRepository classroomRepository;

    @Transactional(readOnly = true)
    public CourseResponseDTO getById(Long id) {
        return courseRepository.findById(id)
                .map(CourseResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
    }

    public List<CourseResponseDTO> searchCourses(Long classroomId, String teacher, String semester) {
        return courseRepository.findAll(AcademicSpecifications.courseSpec(classroomId, teacher, semester))
                .stream().map(CourseResponseDTO::fromEntity).toList();
    }

    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        // 1. Validar que la materia existe
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada"));

        // 2. Validar que el Curso existe
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrada"));

        // 3. Evitar duplicados (Misma Asignatura, mismo Curso, mismo periodo)
        boolean exists = courseRepository.existsBySubjectIdAndClassroomIdAndSchoolYearAndSemester(
                request.getSubjectId(), request.getClassroomId(), request.getSchoolYear(), request.getSemester());

        if (exists) {
            throw new BadRequestException("Ya existe un curso configurado para esta Asignatura en esta aula.");
        }

        Course course = Course.builder()
                .subject(subject)
                .classroom(classroom)
                .schoolYear(request.getSchoolYear())
                .semester(request.getSemester())
                .teacherName(request.getTeacherName())
                .build();

        return CourseResponseDTO.fromEntity(courseRepository.save(course));
    }

    @Transactional
    public CourseResponseDTO updateCourse(Long id, CourseModifyRequestDTO request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        course.setTeacherName(request.getTeacherName());
        course.setSemester(request.getSemester());

        return CourseResponseDTO.fromEntity(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
