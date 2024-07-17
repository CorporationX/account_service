package faang.school.accountservice.dto;

import faang.school.accountservice.model.Account;
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
    private Account account;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
