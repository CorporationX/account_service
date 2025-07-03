package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestDto {

    @NotNull
    private UUID idempotencyToken;
    @NotNull
    private Long userId;
    @NotNull
    private RequestType requestType;
    private String valueLock;
    @NotNull
    private Map<String, String> requestInputData;
}
