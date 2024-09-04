package faang.school.accountservice.dto.account;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffDto {
    private UUID id;
    @NotBlank(message = "Tariff name must be provided!")
    private String name;
    private String rateHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
