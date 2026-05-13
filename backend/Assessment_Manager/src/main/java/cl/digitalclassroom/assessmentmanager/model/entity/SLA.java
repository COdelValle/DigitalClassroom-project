package cl.digitalclassroom.assessmentmanager.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Data
public class SLA {
    private LocalDate fechaInicio, fechaFinal;
    private String tipoOperacion;
    private String ex;
}
