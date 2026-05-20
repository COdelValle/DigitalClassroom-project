package cl.digitalclassroom.assessmentmanager.model.entity;

import cl.digitalclassroom.assessmentmanager.validation.ChileanGrade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @ChileanGrade
    @Column(nullable = false)
    private Double score;

    @CreationTimestamp
    private LocalDateTime registrationDate;
}
