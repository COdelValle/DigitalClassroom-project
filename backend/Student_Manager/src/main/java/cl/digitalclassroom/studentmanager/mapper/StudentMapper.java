package cl.digitalclassroom.studentmanager.mapper;

import cl.digitalclassroom.studentmanager.model.dto.request.StudentRequestDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentFullResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentProfileResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.response.StudentShortResponseDTO;
import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import cl.digitalclassroom.studentmanager.model.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidad Student y sus DTOs.
 * Utiliza MapStruct para generar automáticamente las implementaciones de mapeo.
 *
 * IMPORTANTE: Se utiliza lombok-mapstruct-binding para que MapStruct vea
 * los getters/setters generados por Lombok en tiempo de compilación.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface StudentMapper {

    /**
     * Convierte un StudentRequestDTO a entidad Student.
     * Ignora los campos generados automáticamente (id, timestamps).
     *
     * @param dto DTO de solicitud
     * @return Entidad Student mapeada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student requestDtoToEntity(StudentRequestDTO dto);

    /**
     * Convierte una entidad Student a StudentProfileResponseDTO (vista para docentes).
     *
     * @param entity Entidad Student
     * @return DTO de respuesta de perfil
     */
    @Mapping(target = "fullName", expression = "java(entity.getFirstName() + \" \" + entity.getLastName())")
    @Mapping(target = "emergencyContacts", source = "legalRepresentatives", qualifiedByName = "toEmergencyContactDtos")
    StudentProfileResponseDTO entityToProfileResponseDto(Student entity);

    /**
     * Convierte una entidad Student a StudentFullResponseDTO (vista para administradores).
     *
     * @param entity Entidad Student
     * @return DTO de respuesta completa
     */
    StudentFullResponseDTO entityToFullResponseDto(Student entity);

    /**
     * Convierte una entidad Student a StudentShortResponseDTO (vista para tablas).
     *
     * @param entity Entidad Student
     * @return DTO de respuesta corta
     */
    @Mapping(target = "fullName", expression = "java(entity.getFirstName() + \" \" + entity.getLastName())")
    StudentShortResponseDTO entityToShortResponseDto(Student entity);

    /**
     * Combine los nombres (firstName + lastName) para obtener el nombre completo.
     *
     * @param student Entidad Student
     * @return Nombre completo formateado
     */
    @Named("getFullName")
    default String getFullName(Student student) {
        if (student.getFirstName() == null || student.getLastName() == null) {
            return "";
        }
        return student.getFirstName() + " " + student.getLastName();
    }

    /**
     * Convierte la lista de LegalRepresentativeDTO a EmergencyContactDTO para la vista de perfil.
     *
     * @param legalReps Lista de representantes legales
     * @return Lista de contactos de emergencia
     */
    @Named("toEmergencyContactDtos")
    default List<StudentProfileResponseDTO.EmergencyContactDTO> toEmergencyContactDtos(List<LegalRepresentativeDTO> legalReps) {
        if (legalReps == null) {
            return List.of();
        }
        return legalReps.stream()
                .map(rep -> StudentProfileResponseDTO.EmergencyContactDTO.builder()
                        .name(rep.getFullName())
                        .phoneNumbers(rep.getPhoneNumber())
                        .relationship(rep.getRelationship())
                        .build())
                .collect(Collectors.toList());
    }
}

