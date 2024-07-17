package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
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
    private Long id;
    @NotBlank
    private String name;
    private List<Double> rateHistory;
    private LocalDateTime cratedAt;
    private LocalDateTime updatedAt;
}
