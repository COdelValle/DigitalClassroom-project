package cl.digitalclassroom.classroommanager.model.specifications;

import cl.digitalclassroom.classroommanager.model.entity.Classroom;
import cl.digitalclassroom.classroommanager.model.entity.Course;
import cl.digitalclassroom.classroommanager.model.entity.Subject;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AcademicSpecifications {

    // Filtros para Subject
    public static Specification<Subject> subjectSpec(String name, String area) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (area != null) predicates.add(cb.equal(root.get("area"), area));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Filtros para Classroom
    public static Specification<Classroom> classroomSpec(String name, Integer year) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (year != null) predicates.add(cb.equal(root.get("schoolYear"), year));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Filtros para Course (El más potente)
    public static Specification<Course> courseSpec(Long classroomId, String teacherName, String semester) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (classroomId != null) predicates.add(cb.equal(root.get("classroom").get("id"), classroomId));
            if (teacherName != null) predicates.add(cb.like(cb.lower(root.get("teacherName")), "%" + teacherName.toLowerCase() + "%"));
            if (semester != null) predicates.add(cb.equal(root.get("semester"), semester));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
