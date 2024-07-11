package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsAccountDto {
    @NotNull
    private Long id;
    @NotBlank
    private String account;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
