package cl.digitalclassroom.assessmentmanager.model.specification;

import cl.digitalclassroom.assessmentmanager.model.entity.Assessment;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class AssessmentSpecifications {

    public static Specification<Assessment> hasCourseId(Long courseId) {
        return (root, query, cb) ->
                courseId == null ? cb.conjunction() : cb.equal(root.get("subjectId"), courseId);
    }

    public static Specification<Assessment> titleContains(String title) {
        return (root, query, cb) ->
                title == null ? cb.conjunction() : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Assessment> hasExamDate(Date examDate) {
        return (root, query, cb) ->
                examDate == null ? cb.conjunction() : cb.equal(root.get("examDate"), examDate);
    }
}