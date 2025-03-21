package faang.school.accountservice.dto.request;

import faang.school.accountservice.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestCreateDto {
    @NotBlank
    @Size(min = 36, max = 36)
    private String idempotentKey;
    @Size(min = 36, max = 36)
    private String authorAccountNumber;
    @Size(min = 36, max = 36)
    private String receiverAccountNumber;
    @NotEmpty
    private Map<String, Object> payload;
    @NotNull
    private TransactionType transactionType;
}
