package cl.digitalclassroom.studentmanager.service;

import cl.digitalclassroom.studentmanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.studentmanager.exception.StudentAlreadyExistsException;
import cl.digitalclassroom.studentmanager.mapper.StudentMapper;
import cl.digitalclassroom.studentmanager.model.dto.response.*;
import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.entity.Student;
import cl.digitalclassroom.studentmanager.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión de estudiantes.
 * Implementa la lógica CRUD y operaciones específicas del dominio.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    /**
     * Crea un nuevo estudiante en el sistema.
     * Valida que el RUT no exista previamente.
     *
     * @param request DTO con información del estudiante
     * @return StudentProfileResponseDTO con datos del estudiante creado
     * @throws StudentAlreadyExistsException Si el RUT ya existe
     */
    @Transactional
    public StudentProfileResponseDTO create(StudentRequestDTO request) {
        // Valida que el RUT no esté duplicado
        if (studentRepository.existsByRut(request.getRut())) {
            log.warn("Intento de crear estudiante con RUT duplicado: {}", request.getRut());
            throw new StudentAlreadyExistsException(
                "Ya existe un estudiante con el RUT: " + request.getRut()
            );
        }

        // Convierte DTO a entidad usando mapper
        Student student = studentMapper.requestDtoToEntity(request);
        Student saved = studentRepository.save(student);

        log.info("Estudiante creado exitosamente con ID: {} y RUT: {}", saved.getId(), saved.getRut());
        return studentMapper.entityToProfileResponseDto(saved);
    }

    /**
     * Obtiene todos los estudiantes con soporte para paginación.
     * Retorna una vista simplificada para uso en tablas.
     *
     * @param pageable Parámetros de paginación (página, tamaño, ordenamiento)
     * @return Página con StudentShortResponseDTO
     */
    @Transactional(readOnly = true)
    public Page<StudentShortResponseDTO> findAllForTable(Pageable pageable) {
        log.debug("Obteniendo estudiantes con paginación: {}", pageable);
        return studentRepository.findAll(pageable)
                .map(studentMapper::entityToShortResponseDto);
    }

    /**
     * Obtiene todos los estudiantes sin paginación (para compatibilidad).
     * NO recomendado para grandes volúmenes. Usar findAllForTable con Pageable.
     *
     * @return Lista de StudentShortResponseDTO
     */
    @Transactional(readOnly = true)
    public List<StudentShortResponseDTO> findAllForTableLegacy() {
        log.debug("Obteniendo todos los estudiantes (vista simplificada)");
        return studentRepository.findAll().stream()
                .map(studentMapper::entityToShortResponseDto)
                .toList();
    }

    /**
     * Obtiene el perfil de un estudiante por su ID.
     * Vista pensada para docentes (información relevante sin datos sensibles).
     *
     * @param id Identificador del estudiante
     * @return StudentProfileResponseDTO con información del perfil
     * @throws ResourceNotFoundException Si el estudiante no existe
     */
    @Transactional(readOnly = true)
    public StudentProfileResponseDTO findProfileById(Long id) {
        log.debug("Buscando perfil del estudiante con ID: {}", id);
        return studentRepository.findById(id)
                .map(student -> {
                    log.debug("Perfil encontrado para estudiante ID: {}", id);
                    return studentMapper.entityToProfileResponseDto(student);
                })
                .orElseThrow(() -> {
                    String mensaje = "Estudiante no encontrado con ID: " + id;
                    log.warn(mensaje);
                    return new ResourceNotFoundException(mensaje);
                });
    }

    /**
     * Obtiene los datos completos de un estudiante por su ID.
     * Vista pensada para administradores (información completa y sensible).
     *
     * @param id Identificador del estudiante
     * @return StudentFullResponseDTO con información completa
     * @throws ResourceNotFoundException Si el estudiante no existe
     */
    @Transactional(readOnly = true)
    public StudentFullResponseDTO findFullDetailById(Long id) {
        log.debug("Buscando detalle completo del estudiante con ID: {}", id);
        return studentRepository.findById(id)
                .map(student -> {
                    log.debug("Detalle completo encontrado para estudiante ID: {}", id);
                    return studentMapper.entityToFullResponseDto(student);
                })
                .orElseThrow(() -> {
                    String mensaje = "Estudiante no encontrado con ID: " + id;
                    log.warn(mensaje);
                    return new ResourceNotFoundException(mensaje);
                });
    }

    /**
     * Busca un estudiante por su RUT.
     *
     * @param rut RUT del estudiante a buscar
     * @return StudentFullResponseDTO si existe
     * @throws ResourceNotFoundException Si el estudiante no existe
     */
    @Transactional(readOnly = true)
    public StudentFullResponseDTO findByRut(String rut) {
        log.debug("Buscando estudiante por RUT: {}", rut);
        return studentRepository.findByRut(rut)
                .map(student -> {
                    log.debug("Estudiante encontrado por RUT: {}", rut);
                    return studentMapper.entityToFullResponseDto(student);
                })
                .orElseThrow(() -> {
                    String mensaje = "Estudiante no encontrado con RUT: " + rut;
                    log.warn(mensaje);
                    return new ResourceNotFoundException(mensaje);
                });
    }

    /**
     * Actualiza los datos de un estudiante existente.
     * No permite cambiar el RUT (es único y de identificación).
     *
     * @param id Identificador del estudiante
     * @param request DTO con datos actualizados
     * @return StudentFullResponseDTO con datos actualizados
     * @throws ResourceNotFoundException Si el estudiante no existe
     */
    @Transactional
    public StudentFullResponseDTO update(Long id, StudentRequestDTO request) {
        log.info("Iniciando actualización del estudiante con ID: {}", id);

        // Valida que el estudiante exista
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> {
                    String mensaje = "No se puede actualizar, ID no existe: " + id;
                    log.warn(mensaje);
                    return new ResourceNotFoundException(mensaje);
                });

        // Valida que el RUT no sea duplicado si ha cambiado
        if (!existingStudent.getRut().equals(request.getRut()) &&
            studentRepository.existsByRut(request.getRut())) {
            log.warn("Intento de actualizar estudiante con RUT duplicado: {}", request.getRut());
            throw new StudentAlreadyExistsException(
                "Ya existe otro estudiante con el RUT: " + request.getRut()
            );
        }

        // Reconstruye la entidad con los nuevos datos
        Student updatedStudent = Student.builder()
                .id(existingStudent.getId())
                .rut(request.getRut())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .allergies(request.getAllergies())
                .legalRepresentatives(request.getLegalRepresentatives())
                .createdAt(existingStudent.getCreatedAt())
                .build();

        Student saved = studentRepository.save(updatedStudent);
        log.info("Estudiante con ID: {} actualizado exitosamente", id);
        return studentMapper.entityToFullResponseDto(saved);
    }

    /**
     * Elimina un estudiante del sistema.
     *
     * @param id Identificador del estudiante a eliminar
     * @throws ResourceNotFoundException Si el estudiante no existe
     */
    @Transactional
    public void delete(Long id) {
        log.info("Iniciando eliminación del estudiante con ID: {}", id);

        if (!studentRepository.existsById(id)) {
            String mensaje = "No se puede borrar, ID no existe: " + id;
            log.warn(mensaje);
            throw new ResourceNotFoundException(mensaje);
        }

        studentRepository.deleteById(id);
        log.info("Estudiante con ID: {} eliminado exitosamente", id);
    }

    /**
     * Obtiene el total de estudiantes registrados.
     *
     * @return Cantidad total de estudiantes
     */
    @Transactional(readOnly = true)
    public long countTotal() {
        long count = studentRepository.count();
        log.debug("Total de estudiantes en el sistema: {}", count);
        return count;
    }
}

