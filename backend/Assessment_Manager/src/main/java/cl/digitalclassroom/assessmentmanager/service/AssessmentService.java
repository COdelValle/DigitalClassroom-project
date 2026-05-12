package cl.digitalclassroom.assessmentmanager.service;

import cl.digitalclassroom.assessmentmanager.exception.BadRequestException;
import cl.digitalclassroom.assessmentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.assessmentmanager.model.dto.request.AssessmentModifyRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.request.AssessmentRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.response.AssessmentResponseDTO;
import cl.digitalclassroom.assessmentmanager.model.entity.Assessment;
import cl.digitalclassroom.assessmentmanager.model.entity.Grade;
import cl.digitalclassroom.assessmentmanager.model.specification.AssessmentSpecifications;
import cl.digitalclassroom.assessmentmanager.repository.AssessmentRepository;
import cl.digitalclassroom.assessmentmanager.service.adapter.AcademicServiceAdapter;
import cl.digitalclassroom.assessmentmanager.service.adapter.StudentServiceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final StudentServiceAdapter studentAdapter;
    private final AcademicServiceAdapter academicAdapter;

    @Transactional(readOnly = true)
    public AssessmentResponseDTO assessmentById(Long id) {
        return assessmentRepository.findById(id)
                .map(AssessmentResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("El encargo con ID " + id + " no existe."));
    }

    @Transactional(readOnly = true)
    public List<AssessmentResponseDTO> searchAssessments(String subjectId, String classId, String title, Date examDate) {
        Specification<Assessment> spec = Specification.where((root, query, cb) -> cb.conjunction());

        if (subjectId != null && !subjectId.isBlank()) {
            spec = spec.and(AssessmentSpecifications.hasSubjectId(subjectId));
        }

        if (classId != null && !classId.isBlank()) {
            spec = spec.and(AssessmentSpecifications.hasClassId(classId));
        }

        if (title != null && !title.isBlank()) {
            spec = spec.and(AssessmentSpecifications.titleContains(title));
        }

        if (examDate != null) {
            spec = spec.and(AssessmentSpecifications.hasExamDate(examDate));
        }

        return assessmentRepository.findAll(spec)
                .stream()
                .map(AssessmentResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AssessmentResponseDTO createAssessment(AssessmentRequestDTO request) {

        // 1. Validar que la CLASE existe
        if (!academicAdapter.classExists(request.getClassId())) {
            throw new ResourceNotFoundException("La clase con ID " + request.getClassId() + " no existe.");
        }

        // 2. Validar que la ASIGNATURA existe
        if (!academicAdapter.subjectExists(request.getSubjectId())) {
            throw new ResourceNotFoundException("La asignatura con ID " + request.getSubjectId() + " no existe.");
        }

        // 3. Preparar la entidad Assessment
        Assessment assessment = Assessment.builder()
                .title(request.getTitle())
                .subjectId(request.getSubjectId())
                .classId(request.getClassId())
                .examDate(request.getExamDate())
                .isGraded(request.getGrades() != null && !request.getGrades().isEmpty())
                .build();

        // 4. Si incluye notas, validar cada ESTUDIANTE mediante su propio adapter
        if (assessment.isGraded()) {
            List<Grade> gradeEntities = request.getGrades().stream()
                    .map(dto -> {
                        if (!studentAdapter.studentExists(dto.getStudentId())) {
                            throw new ResourceNotFoundException("Estudiante " + dto.getStudentId() + " no encontrado.");
                        }
                        return Grade.builder()
                                .studentId(dto.getStudentId())
                                .score(dto.getScore())
                                .registrationDate(LocalDateTime.now())
                                .build();
                    }).toList();
            assessment.setGrades(gradeEntities);
        }

        // 5. Guardar y retornar usando el método estático del DTO
        Assessment saved = assessmentRepository.save(assessment);
        return AssessmentResponseDTO.fromEntity(saved);
    }

    @Transactional
    public AssessmentResponseDTO updateAssessment(AssessmentModifyRequestDTO request) {
        // 1. Buscar el encargo existente
        Assessment assessment = assessmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el encargo con ID: " + request.getId()));

        // 2. Validar y actualizar campos de forma selectiva (Evitamos pisar con null)
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            assessment.setTitle(request.getTitle());
        }

        if (request.getExamDate() != null) {
            assessment.setExamDate(request.getExamDate());
        }

        // 3. Validaciones externas (si cambian el curso o materia, hay que validar que existan)
        if (request.getClassId() != null && !request.getClassId().equals(assessment.getClassId())) {
            if (!academicAdapter.classExists(request.getClassId())) {
                throw new BadRequestException("La nueva clase indicada no existe");
            }
            assessment.setClassId(request.getClassId());
        }

        if (request.getSubjectId() != null && !request.getSubjectId().equals(assessment.getSubjectId())) {
            if (!academicAdapter.subjectExists(request.getSubjectId())) {
                throw new BadRequestException("La nueva asignatura indicada no existe");
            }
            assessment.setSubjectId(request.getSubjectId());
        }

        // 4. Guardar cambios y retornar
        return AssessmentResponseDTO.fromEntity(assessmentRepository.save(assessment));
    }

    @Transactional
    public void deleteAssessment(Long id) {
        // 1. Buscar la evaluación
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la evaluación con ID: " + id));

        // 2. Obtener el año actual de ejecución
        int currentYear = LocalDate.now().getYear();
        // Obtener el año de la evaluación
        int assessmentYear = assessment.getExamDate().getYear();

        // 3. Aplicar Regla: No eliminar si es del año actual Y tiene notas
        if (assessmentYear == currentYear && !assessment.getGrades().isEmpty()) {
            throw new BadRequestException(
                    String.format("Prohibido eliminar: La evaluación pertenece al año escolar actual (%d) y ya tiene notas registradas.", currentYear)
            );
        }

        // 4. (Opcional) Regla estricta: No eliminar nada de años anteriores
        if (assessmentYear < currentYear) {
            throw new BadRequestException("No se pueden eliminar registros de años académicos cerrados.");
        }

        // 5. Si pasa las validaciones, se elimina
        assessmentRepository.delete(assessment);
    }
}
