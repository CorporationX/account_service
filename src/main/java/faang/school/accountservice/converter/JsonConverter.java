package faang.school.accountservice.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.exception.DataConversionException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.Map;

@Converter
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JavaType MAP_TYPE = objectMapper.getTypeFactory()
            .constructMapType(Map.class, String.class, Object.class);

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new DataConversionException("Error converting Map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(dbValue, MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw new DataConversionException("Error converting JSON to Map", e);
        }
    }
}
