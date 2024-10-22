package faang.school.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Error {
    private String code;
    private String message;
    private Map<String, String> errors;
}
