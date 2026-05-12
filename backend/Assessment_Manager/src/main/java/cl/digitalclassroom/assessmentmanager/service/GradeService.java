package cl.digitalclassroom.assessmentmanager.service;

import cl.digitalclassroom.assessmentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.assessmentmanager.model.dto.request.GradeRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.response.AssessmentResponseDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.response.GradeResponseDTO;
import cl.digitalclassroom.assessmentmanager.model.entity.Assessment;
import cl.digitalclassroom.assessmentmanager.model.entity.Grade;
import cl.digitalclassroom.assessmentmanager.model.specification.AssessmentSpecifications;
import cl.digitalclassroom.assessmentmanager.repository.AssessmentRepository;
import cl.digitalclassroom.assessmentmanager.repository.GradeRepository;
import cl.digitalclassroom.assessmentmanager.service.adapter.StudentServiceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final AssessmentRepository assessmentRepository;
    private final StudentServiceAdapter studentAdapter;

    @Transactional(readOnly = true)
    public GradeResponseDTO gradeById(Long id) {
        return gradeRepository.findById(id)
                .map(GradeResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("La nota con ID " + id + " no existe."));
    }

    @Transactional
    public GradeResponseDTO saveIndependentGrade(GradeRequestDTO request) {
        // 1. Validar estudiante mediante el ADAPTER (con Circuit Breaker)
        if (!studentAdapter.studentExists(request.getStudentId())) {
            throw new ResourceNotFoundException("El estudiante no existe o el servicio de alumnos no responde.");
        }

        // 2. Buscar el Assessment (Encargo) al que pertenece
        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        // 3. Crear y guardar la nota
        Grade grade = Grade.builder()
                .studentId(request.getStudentId())
                .score(request.getScore())
                .registrationDate(LocalDateTime.now())
                .build();

        assessment.getGrades().add(grade);

        assessmentRepository.save(assessment);

        // 4. Retornar usando el método estático del DTO
        return GradeResponseDTO.fromEntity(grade);
    }
}
