package cl.digitalclassroom.classroommanager.service;

import cl.digitalclassroom.classroommanager.exception.BadRequestException;
import cl.digitalclassroom.classroommanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.classroommanager.model.dto.request.SubjectRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.SubjectModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.response.SubjectResponseDTO;
import cl.digitalclassroom.classroommanager.model.entity.Subject;
import cl.digitalclassroom.classroommanager.model.specifications.AcademicSpecifications;
import cl.digitalclassroom.classroommanager.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public SubjectResponseDTO getById(Long id) {
        return subjectRepository.findById(id)
                .map(SubjectResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponseDTO> searchSubjects(String name, String area) {
        return subjectRepository.findAll(AcademicSpecifications.subjectSpec(name, area))
                .stream().map(SubjectResponseDTO::fromEntity).toList();
    }

    @Transactional
    public SubjectResponseDTO createSubject(SubjectRequestDTO request) {
        if (subjectRepository.existsByName(request.getName())) {
            throw new BadRequestException("La asignatura " + request.getName() + " ya está registrada.");
        }

        Subject subject = Subject.builder()
                .name(request.getName())
                .area(request.getArea())
                .isActive(true)
                .build();

        return SubjectResponseDTO.fromEntity(subjectRepository.save(subject));
    }

    @Transactional
    public SubjectResponseDTO updateSubject(Long id, SubjectModifyRequestDTO request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada"));

        subject.setName(request.getName());
        subject.setArea(request.getArea());
        return SubjectResponseDTO.fromEntity(subjectRepository.save(subject));
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) throw new ResourceNotFoundException("ID no existe");
        subjectRepository.deleteById(id);
    }
}
