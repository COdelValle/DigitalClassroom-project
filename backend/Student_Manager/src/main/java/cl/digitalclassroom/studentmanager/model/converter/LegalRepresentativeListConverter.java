package cl.digitalclassroom.studentmanager.model.converter;

import cl.digitalclassroom.studentmanager.model.dto.LegalRepresentativeDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter(autoApply = false)
public class LegalRepresentativeListConverter implements AttributeConverter<List<LegalRepresentativeDTO>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<LegalRepresentativeDTO> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to convert legal representatives to JSON", e);
        }
    }

    @Override
    public List<LegalRepresentativeDTO> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(dbData, new TypeReference<List<LegalRepresentativeDTO>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read legal representatives from JSON", e);
        }
    }
}
