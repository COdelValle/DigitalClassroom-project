package cl.digitalclassroom.classroommanager.service;

import cl.digitalclassroom.classroommanager.exception.BadRequestException;
import cl.digitalclassroom.classroommanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.classroommanager.model.dto.request.ClassroomRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.ClassroomModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.response.ClassroomResponseDTO;
import cl.digitalclassroom.classroommanager.model.entity.Classroom;
import cl.digitalclassroom.classroommanager.model.entity.Course;
import cl.digitalclassroom.classroommanager.model.specifications.AcademicSpecifications;
import cl.digitalclassroom.classroommanager.repository.ClassroomRepository;
import cl.digitalclassroom.classroommanager.repository.CourseRepository;
import cl.digitalclassroom.classroommanager.service.adapter.StudentServiceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final CourseRepository courseRepository;
    private final StudentServiceAdapter studentAdapter;

    @Transactional(readOnly = true)
    public ClassroomResponseDTO getById(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula no encontrada"));

        // Al buscar por ID específico, traemos también sus cursos
        List<Course> courses = courseRepository.findByClassroomId(id);
        return ClassroomResponseDTO.fromEntity(classroom, courses);
    }

    @Transactional(readOnly = true)
    public List<ClassroomResponseDTO> searchClassrooms(String name, Integer year) {
        return classroomRepository.findAll(AcademicSpecifications.classroomSpec(name, year))
                .stream().map(c -> ClassroomResponseDTO.fromEntity(c, null)).toList();
    }

    @Transactional
    public ClassroomResponseDTO createClassroom(ClassroomRequestDTO request) {
        log.info("Creando nueva aula: {}", request.getCode());

        // 1. Validar unicidad del código
        if (classroomRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("El código de aula " + request.getCode() + " ya existe.");
        }

        // 2. Validar que los estudiantes existan (vía Feign)
        request.getStudentIds().forEach(id -> {
            if (!studentAdapter.studentExists(id)) {
                throw new ResourceNotFoundException("El estudiante con ID " + id + " no existe.");
            }
        });

        Classroom classroom = Classroom.builder()
                .code(request.getCode())
                .name(request.getName())
                .schoolYear(request.getSchoolYear())
                .studentIds(request.getStudentIds())
                .build();

        return ClassroomResponseDTO.fromEntity(classroomRepository.save(classroom), null);
    }

    @Transactional
    public ClassroomResponseDTO updateClassroom(Long id, ClassroomModifyRequestDTO request) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula no encontrada"));

        classroom.setName(request.getName());
        classroom.setStudentIds(request.getStudentIds());

        return ClassroomResponseDTO.fromEntity(classroomRepository.save(classroom), null);
    }

    @Transactional
    public void deleteClassroom(Long id) {
        if (!courseRepository.findByClassroomId(id).isEmpty()) {
            throw new BadRequestException("No se puede eliminar el aula porque tiene cursos activos.");
        }
        classroomRepository.deleteById(id);
    }
}
