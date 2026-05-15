package cl.digitalclassroom.classroommanager.repository;

import cl.digitalclassroom.classroommanager.model.entity.Course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findByClassroomId(Long classroomId);

    boolean existsBySubjectIdAndClassroomIdAndSchoolYearAndSemester(Long subjectId, Long classroomId, Integer schoolYear, String semester);
}
