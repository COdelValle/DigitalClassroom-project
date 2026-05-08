package cl.digitalclassroom.studentmanager.repository;

import cl.digitalclassroom.studentmanager.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByRut(String rut);
    Optional<Student> findByRut(String rut);
}
