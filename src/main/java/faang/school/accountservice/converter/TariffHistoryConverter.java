package faang.school.accountservice.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Converter(autoApply = true)
public class TariffHistoryConverter implements AttributeConverter<List<BigDecimal>, String> {
    private static final Logger logger = LoggerFactory.getLogger(TariffHistoryConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<BigDecimal> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting tariff history list to JSON", e);
            throw new RuntimeException("Failed to convert tariff history to JSON", e);
        }
    }

    @Override
    public List<BigDecimal> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON to tariff history list", e);
            throw new RuntimeException("Failed to convert JSON to tariff history", e);
        }
    }
}