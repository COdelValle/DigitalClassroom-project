package cl.digitalclassroom.studentmanager.controller;

import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentFullResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentProfileResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentShortResponseDTO;
import cl.digitalclassroom.studentmanager.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de estudiantes.
 * Expone los endpoints CRUD y operaciones específicas del dominio.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Operaciones CRUD para la gestión de alumnos")
public class StudentController {

    private final StudentService studentService;

    /**
     * Crea un nuevo estudiante.
     *
     * @param request DTO con información del estudiante
     * @return ResponseEntity con StudentProfileResponseDTO creado
     */
    @Operation(summary = "Crear nuevo estudiante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentProfileResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o RUT duplicado"),
        @ApiResponse(responseCode = "409", description = "El RUT ya existe en el sistema")
    })
    @PostMapping
    public ResponseEntity<StudentProfileResponseDTO> create(@Valid @RequestBody StudentRequestDTO request) {
        log.info("POST /api/v1/students - Creando nuevo estudiante");
        return new ResponseEntity<>(studentService.create(request), HttpStatus.CREATED);
    }

    /**
     * Obtiene todos los estudiantes con paginación (vista simplificada).
     *
     * @param pageable Parámetros de paginación
     * @return Page con StudentShortResponseDTO
     */
    @Operation(summary = "Listado paginado de estudiantes para tablas")
    @ApiResponse(responseCode = "200", description = "Lista de estudiantes obtenida",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping
    public Page<StudentShortResponseDTO> getAll(
        @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC)
        @Parameter(description = "Parámetros de paginación") Pageable pageable) {
        log.debug("GET /api/v1/students - Obteniendo lista paginada con: {}", pageable);
        return studentService.findAllForTable(pageable);
    }

    /**
     * Obtiene el perfil de un estudiante (vista para docentes).
     *
     * @param id Identificador del estudiante
     * @return StudentProfileResponseDTO con información del perfil
     */
    @Operation(summary = "Obtener perfil por ID (Vista Profesor)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentProfileResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/{id}/profile")
    public StudentProfileResponseDTO getProfile(@PathVariable Long id) {
        log.debug("GET /api/v1/students/{}/profile - Obteniendo perfil del estudiante", id);
        return studentService.findProfileById(id);
    }

    /**
     * Obtiene los datos completos de un estudiante (vista para administradores).
     *
     * @param id Identificador del estudiante
     * @return StudentFullResponseDTO con información completa
     */
    @Operation(summary = "Obtener detalle completo por ID (Vista Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentFullResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/{id}/full")
    public StudentFullResponseDTO getFull(@PathVariable Long id) {
        log.debug("GET /api/v1/students/{}/full - Obteniendo detalle completo del estudiante", id);
        return studentService.findFullDetailById(id);
    }

    /**
     * Obtiene un estudiante por su RUT.
     *
     * @param rut RUT del estudiante a buscar
     * @return StudentFullResponseDTO
     */
    @Operation(summary = "Obtener estudiante por RUT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/rut/{rut}")
    public StudentFullResponseDTO getByRut(@PathVariable String rut) {
        log.debug("GET /api/v1/students/rut/{} - Obteniendo estudiante por RUT", rut);
        return studentService.findByRut(rut);
    }

    /**
     * Actualiza un estudiante existente.
     *
     * @param id Identificador del estudiante
     * @param request DTO con datos actualizados
     * @return StudentFullResponseDTO actualizado
     */
    @Operation(summary = "Actualizar estudiante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudiante actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentFullResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @PutMapping("/{id}")
    public StudentFullResponseDTO update(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO request) {
        log.info("PUT /api/v1/students/{} - Actualizando estudiante", id);
        return studentService.update(id, request);
    }

    /**
     * Elimina un estudiante del sistema.
     *
     * @param id Identificador del estudiante a eliminar
     */
    @Operation(summary = "Eliminar estudiante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Estudiante eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/students/{} - Eliminando estudiante", id);
        studentService.delete(id);
    }

    /**
     * Obtiene el conteo total de estudiantes.
     *
     * @return Cantidad total de estudiantes
     */
    @Operation(summary = "Obtener conteo total de estudiantes")
    @ApiResponse(responseCode = "200", description = "Cantidad de estudiantes")
    @GetMapping("/count")
    public ResponseEntity<Long> getCount() {
        log.debug("GET /api/v1/students/count - Obteniendo conteo total");
        return ResponseEntity.ok(studentService.countTotal());
    }
}

