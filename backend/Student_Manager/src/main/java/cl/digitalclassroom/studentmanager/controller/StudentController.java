package cl.digitalclassroom.studentmanager.controller;

import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentFullResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentProfileResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentShortResponseDTO;
import cl.digitalclassroom.studentmanager.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Operaciones CRUD para la gestión de alumnos")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Crear nuevo estudiante")
    @PostMapping
    public ResponseEntity<StudentProfileResponseDTO> create(@Valid @RequestBody StudentRequestDTO request) {
        return new ResponseEntity<>(studentService.create(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Listado simplificado para tablas")
    @GetMapping
    public List<StudentShortResponseDTO> getAll() {
        return studentService.findAllForTable();
    }

    @Operation(summary = "Obtener perfil por ID (Vista Profesor)")
    @GetMapping("/{id}/profile")
    public StudentProfileResponseDTO getProfile(@PathVariable Long id) {
        return studentService.findProfileById(id);
    }

    @Operation(summary = "Obtener detalle completo por ID (Vista Admin)")
    @GetMapping("/{id}/full")
    public StudentFullResponseDTO getFull(@PathVariable Long id) {
        return studentService.findFullDetailById(id);
    }

    @Operation(summary = "Actualizar estudiante")
    @PutMapping("/{id}")
    public StudentFullResponseDTO update(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO request) {
        return studentService.update(id, request);
    }

    @Operation(summary = "Eliminar estudiante")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
