package cl.digitalclassroom.assessmentmanager.service;

import cl.digitalclassroom.assessmentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.assessmentmanager.model.dto.request.AssessmentRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.response.AssessmentResponseDTO;
import cl.digitalclassroom.assessmentmanager.model.entity.Assessment;
import cl.digitalclassroom.assessmentmanager.model.entity.Grade;
import cl.digitalclassroom.assessmentmanager.repository.AssessmentRepository;
import cl.digitalclassroom.assessmentmanager.service.adapter.AcademicServiceAdapter;
import cl.digitalclassroom.assessmentmanager.service.adapter.StudentServiceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final StudentServiceAdapter studentAdapter;
    private final AcademicServiceAdapter academicAdapter; // El nuevo adapter

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
}
