package cl.digitalclassroom.assessmentmanager.service;

import cl.digitalclassroom.assessmentmanager.exception.BadRequestException;
import cl.digitalclassroom.assessmentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.assessmentmanager.model.dto.request.GradeRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.request.modify.GradeModifyRequestDTO;
import cl.digitalclassroom.assessmentmanager.model.dto.response.GradeResponseDTO;
import cl.digitalclassroom.assessmentmanager.model.entity.Assessment;
import cl.digitalclassroom.assessmentmanager.model.entity.Grade;
import cl.digitalclassroom.assessmentmanager.model.specification.GradeSpecifications;
import cl.digitalclassroom.assessmentmanager.repository.AssessmentRepository;
import cl.digitalclassroom.assessmentmanager.repository.GradeRepository;
import cl.digitalclassroom.assessmentmanager.service.adapter.StudentServiceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Transactional(readOnly = true)
    public List<GradeResponseDTO> searchGrades(Long studentId, Double minScore, Double maxScore, LocalDate date) {

        Specification<Grade> spec = (root, query, cb) -> cb.conjunction();

        if (studentId != null) {
            spec = spec.and(GradeSpecifications.hasStudentId(studentId));
        }

        if (minScore != null && maxScore != null) {
            spec = spec.and(GradeSpecifications.scoreBetween(minScore, maxScore));
        } else if (minScore != null) {
            spec = spec.and(GradeSpecifications.scoreGreaterThan(minScore));
        } else if (maxScore != null) {
            spec = spec.and(GradeSpecifications.scoreLessThan(maxScore));
        }

        if (date != null) {
            spec = spec.and(GradeSpecifications.registeredOn(date));
        }

        return gradeRepository.findAll(spec).stream()
                .map(GradeResponseDTO::fromEntity)
                .toList();
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

    @Transactional
    public GradeResponseDTO updateGrade(Long id, GradeModifyRequestDTO request) {
        // 1. Buscar la nota existente
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada con ID: " + id));

        // 2. Actualizar el puntaje si viene en el request
        if (request.getScore() != null) {
            grade.setScore(request.getScore());
        }

        // 3. Si cambian el estudiante, validar que el nuevo exista
        if (request.getStudentId() != null && !request.getStudentId().equals(grade.getStudentId())) {
            if (!studentAdapter.studentExists(request.getStudentId())) {
                throw new ResourceNotFoundException("El nuevo estudiante indicado no existe.");
            }
            grade.setStudentId(request.getStudentId());
        }

        // 4. Guardar y retornar
        return GradeResponseDTO.fromEntity(gradeRepository.save(grade));
    }

    @Transactional
    public void deleteGrade(Long id) {
        // 1. Buscar la nota
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la nota con ID: " + id));

        // 2. Validación de seguridad: No borrar notas de años anteriores
        // registrationDate es LocalDateTime, así que usamos .getYear()
        int currentYear = LocalDate.now().getYear();
        if (grade.getRegistrationDate().getYear() < currentYear) {
            throw new BadRequestException("No es posible eliminar notas de periodos académicos anteriores.");
        }

        // 3. Ejecutar el borrado
        gradeRepository.delete(grade);
    }
}
