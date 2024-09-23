package faang.school.accountservice.converter;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MapFieldsConverter {

    public <T extends Enum<T>> T convertToEnum(Map<String, Object> map, String key, Class<T> enumType) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Key '" + key + "' not found in map or value is null");
        }

        if (value instanceof String) {
            try {
                return Enum.valueOf(enumType, (String) value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid value for enum " + enumType.getSimpleName() + ": " + value, e);
            }
        } else {
            throw new IllegalArgumentException("Value for key '" + key + "' is not a string: " + value);
        }
    }
}
