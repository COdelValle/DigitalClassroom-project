package cl.digitalclassroom.studentmanager.service;

import cl.digitalclassroom.studentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.studentmanager.model.dto.response.*;
import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.entity.Student;
import cl.digitalclassroom.studentmanager.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    // CREAR
    @Transactional
    public StudentProfileResponseDTO create(StudentRequestDTO request) {
        if (studentRepository.existsByRut(request.getRut())) {
            throw new RuntimeException("Ya existe un estudiante con el RUT: " + request.getRut());
        }
        Student student = mapRequestToEntity(request);
        Student saved = studentRepository.save(student);
        log.info("Estudiante creado con ID: {}", saved.getId());
        return mapEntityToProfileResponse(saved);
    }

    // LISTAR
    @Transactional(readOnly = true)
    public List<StudentShortResponseDTO> findAllForTable() {
        return studentRepository.findAll().stream()
                .map(entity -> StudentShortResponseDTO.builder()
                        .id(entity.getId())
                        .rut(entity.getRut())
                        .fullName(entity.getFirstName() + " " + entity.getLastName())
                        .build())
                .toList();
    }

    // BUSCAR POR ID (Básica)
    @Transactional(readOnly = true)
    public StudentProfileResponseDTO findProfileById(Long id) {
        return studentRepository.findById(id)
                .map(this::mapEntityToProfileResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    // BUSCAR POR ID (Admin)
    @Transactional(readOnly = true)
    public StudentFullResponseDTO findFullDetailById(Long id) {
        return studentRepository.findById(id)
                .map(this::mapEntityToFullResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    // ACTUALIZAR: Retorna Full para que el Admin confirme cambios
    @Transactional
    public StudentFullResponseDTO update(Long id, StudentRequestDTO request) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar, ID no existe: " + id));

        Student updatedStudent = Student.builder()
                .id(existingStudent.getId())
                .rut(existingStudent.getRut())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .allergies(request.getAllergies())
                .legalRepresentatives(request.getLegalRepresentatives())
                .build();

        Student saved = studentRepository.save(updatedStudent);
        log.info("Estudiante con ID: {} actualizado correctamente", id);
        return mapEntityToFullResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede borrar, ID no existe: " + id);
        }
        studentRepository.deleteById(id);
        log.info("Estudiante con ID: {} eliminado", id);
    }

    // MÉTODOS PRIVADOS DE MAPEO
    // Mapea la Request para pasarla a una Entidad viable
    private Student mapRequestToEntity(StudentRequestDTO request) {
        return Student.builder()
                .rut(request.getRut())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .allergies(request.getAllergies())
                .legalRepresentatives(request.getLegalRepresentatives())
                .build();
    }

    // Mapeo para Vista Profesor (Seguro)
    private StudentProfileResponseDTO mapEntityToProfileResponse(Student entity) {
        return StudentProfileResponseDTO.builder()
                .id(entity.getId())
                .rut(entity.getRut())
                .fullName(entity.getFirstName() + " " + entity.getLastName())
                .allergies(entity.getAllergies())
                .emergencyContacts(entity.getLegalRepresentatives().stream()
                        .map(lr -> StudentProfileResponseDTO.EmergencyContactDTO.builder()
                                .name(lr.getFullName())
                                .phoneNumbers(lr.getPhoneNumber())
                                .relationship(lr.getRelationship())
                                .build())
                        .toList())
                .build();
    }

    // Mapeo para Vista Admin (Completo)
    private StudentFullResponseDTO mapEntityToFullResponse(Student entity) {
        return StudentFullResponseDTO.builder()
                .id(entity.getId())
                .rut(entity.getRut())
                .firstName(entity.getFirstName())
                .middleName(entity.getMiddleName())
                .lastName(entity.getLastName())
                .birthDate(entity.getBirthDate())
                .allergies(entity.getAllergies())
                .legalRepresentatives(entity.getLegalRepresentatives()) // Mapeo directo de la lista
                .build();
    }
}