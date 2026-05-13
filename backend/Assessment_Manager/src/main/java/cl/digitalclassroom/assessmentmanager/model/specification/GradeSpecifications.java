package cl.digitalclassroom.assessmentmanager.model.specification;

import cl.digitalclassroom.assessmentmanager.model.entity.Grade;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class GradeSpecifications {

    public static Specification<Grade> hasStudentId(Long studentId) {
        return (root, query, cb) ->
                studentId == null ? cb.conjunction() : cb.equal(root.get("studentId"), studentId);
    }

    public static Specification<Grade> scoreGreaterThan(Double minScore) {
        return (root, query, cb) ->
                minScore == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("score"), minScore);
    }

    public static Specification<Grade> scoreLessThan(Double maxScore) {
        return (root, query, cb) ->
                maxScore == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("score"), maxScore);
    }

    public static Specification<Grade> scoreBetween(Double min, Double max) {
        return (root, query, cb) ->
                (min == null || max == null) ? cb.conjunction() : cb.between(root.get("score"), min, max);
    }

    public static Specification<Grade> registeredOn(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) return cb.conjunction();
            return cb.equal(root.get("registrationDate").as(LocalDate.class), date);
        };
    }
}