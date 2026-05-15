package cl.digitalclassroom.classroommanager.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "classroom")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "school_year", nullable = false)
    private Integer schoolYear;

    @ElementCollection
    @CollectionTable(name = "classroom_students", joinColumns = @JoinColumn(name = "classroom_id"))
    @Column(name = "student_id")
    private List<Long> studentIds;
}
