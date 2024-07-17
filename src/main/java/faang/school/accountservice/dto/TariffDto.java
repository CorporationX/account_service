package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    private List<Double> rateHistory;
    private LocalDateTime cratedAt;
    private LocalDateTime updatedAt;
}
