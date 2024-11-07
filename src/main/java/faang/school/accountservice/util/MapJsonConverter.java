package faang.school.accountservice.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Converter
public class MapJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not convert Map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not convert JSON to Map", e);
        }
    }
}
