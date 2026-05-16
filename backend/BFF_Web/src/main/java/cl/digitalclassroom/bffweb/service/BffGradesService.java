package cl.digitalclassroom.bffweb.service;

import cl.digitalclassroom.bffweb.client.AssessmentFeignClient;
import cl.digitalclassroom.bffweb.client.ClassroomFeignClient;
import cl.digitalclassroom.bffweb.client.StudentFeignClient;
import cl.digitalclassroom.bffweb.dto.*;
import cl.digitalclassroom.bffweb.dto.external.ClassroomResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.CourseResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.GradeResponseDTO;
import cl.digitalclassroom.bffweb.dto.external.StudentProfileResponseDTO;
import cl.digitalclassroom.bffweb.dto.request.GradeRequestDTO;
import cl.digitalclassroom.bffweb.dto.response.BffGradeSearchResponseDTO;
import cl.digitalclassroom.bffweb.dto.response.StudentReportCardDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BffGradesService {

    private final StudentFeignClient studentClient;
    private final ClassroomFeignClient classroomClient;
    private final AssessmentFeignClient assessmentClient;

    /**
     * Lógica de negocio para armar la Ficha de Rendimiento Completa (Report Card)
     */
    @CircuitBreaker(name = "assessmentService", fallbackMethod = "getStudentReportCardFallback")
    public StudentReportCardDTO getStudentReportCard(Long studentId) {
        StudentProfileResponseDTO student = studentClient.getStudentById(studentId);

        int currentYear = LocalDate.now().getYear();
        List<ClassroomResponseDTO> classrooms = classroomClient.searchClassrooms(null, currentYear);

        ClassroomResponseDTO studentClassroom = classrooms.stream()
                .filter(c -> c.getStudentIds() != null && c.getStudentIds().contains(studentId))
                .findFirst()
                .orElse(null);

        List<GradeResponseDTO> flatGrades = assessmentClient.searchGrades(studentId, null, null, null);
        List<SubjectGradeDTO> subjectsList = new ArrayList<>();

        if (studentClassroom != null) {
            List<CourseResponseDTO> coursesInClassroom = classroomClient.searchCourses(studentClassroom.getId(), null, null);

            for (CourseResponseDTO course : coursesInClassroom) {
                List<GradeDetailDTO> details = flatGrades.stream()
                        .map(g -> GradeDetailDTO.builder()
                                .gradeId(g.getId())
                                .value(g.getScore())
                                .title("Evaluación: " + g.getRegistrationDate().toLocalDate())
                                .build())
                        .collect(Collectors.toList());

                double scoreSum = flatGrades.stream().mapToDouble(GradeResponseDTO::getScore).sum();
                double courseAverage = flatGrades.isEmpty() ? 1.0 : scoreSum / flatGrades.size();
                courseAverage = Math.round(courseAverage * 10.0) / 10.0;

                subjectsList.add(SubjectGradeDTO.builder()
                        .courseId(course.getId())
                        .subjectName(course.getSubject() != null ? course.getSubject().getName() : "Asignatura sin Nombre")
                        .teacherName(course.getTeacherName())
                        .individualGrades(details)
                        .average(courseAverage)
                        .build());
            }
        }

        double totalSum = flatGrades.stream().mapToDouble(GradeResponseDTO::getScore).sum();
        double finalGeneralAverage = flatGrades.isEmpty() ? 1.0 : totalSum / flatGrades.size();
        finalGeneralAverage = Math.round(finalGeneralAverage * 10.0) / 10.0;

        return StudentReportCardDTO.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .rut(student.getRut())
                .classroomName(studentClassroom != null ? studentClassroom.getName() : "Sin Aula Asignada")
                .subjects(subjectsList)
                .finalAverage(finalGeneralAverage)
                .build();
    }

    /**
     * Lógica de negocio para realizar búsquedas enriquecidas
     */
    public List<BffGradeSearchResponseDTO> searchBffGrades(Long studentId, Double minScore, Double maxScore, String date) {
        List<GradeResponseDTO> rawGrades = assessmentClient.searchGrades(studentId, minScore, maxScore, date);
        List<BffGradeSearchResponseDTO> enrichedResults = new ArrayList<>();

        for (GradeResponseDTO grade : rawGrades) {
            StudentProfileResponseDTO studentDetail = studentClient.getStudentById(grade.getStudentId());

            List<ClassroomResponseDTO> classrooms = classroomClient.searchClassrooms(null, LocalDate.now().getYear());
            ClassroomResponseDTO classroomDetail = classrooms.stream()
                    .filter(c -> c.getStudentIds() != null && c.getStudentIds().contains(grade.getStudentId()))
                    .findFirst()
                    .orElse(null);

            enrichedResults.add(BffGradeSearchResponseDTO.builder()
                    .gradeId(grade.getId())
                    .gradeValue(grade.getScore())
                    .title("Nota Parcial Registrada")
                    .date(grade.getRegistrationDate().toLocalDate())
                    .studentId(studentDetail.getId())
                    .studentName(studentDetail.getFullName())
                    .studentRut(studentDetail.getRut())
                    .courseId(classroomDetail != null ? classroomDetail.getId() : null)
                    .subjectName(classroomDetail != null ? classroomDetail.getName() : "Sin Grupo Curso")
                    .build());
        }
        return enrichedResults;
    }

    /**
     * Lógica de negocio para agregar una nota con validación cruzada previa
     */
    public GradeResponseDTO addGrade(GradeRequestDTO request) {
        studentClient.getStudentById(request.getStudentId());
        return assessmentClient.saveGrade(request);
    }

    /**
     * Lógica de negocio para modificar nota
     */
    public GradeResponseDTO modifyGrade(Long gradeId, GradeRequestDTO request) {
        return assessmentClient.updateGrade(gradeId, request);
    }

    /**
     * MÈTODO FALLBACK para el Circuit Breaker de report card
     */
    public StudentReportCardDTO getStudentReportCardFallback(Long studentId, Throwable e) {
        String studentName = "Estudiante no disponible temporalmente";
        String rut = "N/A";
        try {
            StudentProfileResponseDTO student = studentClient.getStudentById(studentId);
            studentName = student.getFullName();
            rut = student.getRut();
        } catch (Exception ignored) {}

        return StudentReportCardDTO.builder()
                .studentId(studentId)
                .studentName(studentName)
                .rut(rut)
                .classroomName("Módulo de notas en mantenimiento")
                .subjects(new ArrayList<>())
                .finalAverage(1.0)
                .build();
    }
}
