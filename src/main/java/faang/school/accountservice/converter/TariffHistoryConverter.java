package faang.school.accountservice.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Converter()
@Slf4j
@RequiredArgsConstructor
public class TariffHistoryConverter implements AttributeConverter<List<Long>, String> {
    private static final String MESSAGE_ERROR = "Failed to convert long list of tariff history to JSON: %s";

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<Long> longs) {
        try {
            return objectMapper.writeValueAsString(longs);
        } catch (JsonProcessingException e) {
            String message = String.format(MESSAGE_ERROR, e);
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to tariff history", e);
        }
    }
}
