package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class RequestUpdateDto {
//    @NotNull
//    private UUID idempotencyKey;

    @NotNull
    private Long userId;

    @NotNull
    private RequestType requestType;

    private Map<String, Object> inputParams;

    private String description;
}
