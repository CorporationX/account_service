package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateRequestDto(

        @NotBlank(message = "Idempotency token cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "Invalid UUID format")
        String idempotencyToken,

        @NotNull(message = "Payment account id can not be null")
        @Positive(message = "Payment account id must be positive")
        Long paymentAccountId,

        @NotNull(message = "Request type can not be null")
        RequestType requestType,

        Map<String, Object> input
) {
}
