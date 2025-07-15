package faang.school.accountservice.dto;

import faang.school.accountservice.enums.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateStatusDto {
    @NotNull
    private UUID idempotencyKey;

    @NotNull
    private RequestStatus requestStatus;

    @NotBlank
    private String statusDetails;
}
