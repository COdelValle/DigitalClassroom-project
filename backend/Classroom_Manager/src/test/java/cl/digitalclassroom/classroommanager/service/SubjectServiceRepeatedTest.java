package cl.digitalclassroom.classroommanager.service;

import cl.digitalclassroom.classroommanager.exception.BadRequestException;
import cl.digitalclassroom.classroommanager.exception.ResourceNotFoundException;
import cl.digitalclassroom.classroommanager.model.dto.request.SubjectRequestDTO;
import cl.digitalclassroom.classroommanager.model.dto.request.modify.SubjectModifyRequestDTO;
import cl.digitalclassroom.classroommanager.model.entity.Subject;
import cl.digitalclassroom.classroommanager.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceRepeatedTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    private Subject subject;
    private SubjectRequestDTO subjectRequest;
    private SubjectModifyRequestDTO subjectModifyRequest;

    @BeforeEach
    void setUp() {
        subject = Subject.builder().id(1L).name("Historia").area("Ciencias Sociales").isActive(true).build();

        subjectRequest = new SubjectRequestDTO();
        subjectRequest.setName("Historia");
        subjectRequest.setArea("Ciencias Sociales");

        subjectModifyRequest = new SubjectModifyRequestDTO();
        subjectModifyRequest.setName("Historia Actualizada");
        subjectModifyRequest.setArea("Ciencias Sociales");
    }

    @RepeatedTest(45)
    void repeatedCreateAndUpdateSubjectFlow() {
        when(subjectRepository.existsByName("Historia")).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        var created = subjectService.createSubject(subjectRequest);
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Historia");

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        var updated = subjectService.updateSubject(1L, subjectModifyRequest);
        assertThat(updated.getName()).isEqualTo("Historia Actualizada");
    }

    @RepeatedTest(1)
    void repeatedCreateSubjectThrowsWhenNameExists() {
        when(subjectRepository.existsByName("Historia")).thenReturn(true);

        assertThatThrownBy(() -> subjectService.createSubject(subjectRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ya está registrada");
    }

    @RepeatedTest(1)
    void repeatedDeleteSubjectThrowsWhenIdNotFound() {
        when(subjectRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> subjectService.deleteSubject(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ID no existe");
    }
}
