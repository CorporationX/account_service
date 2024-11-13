package faang.school.accountservice.dto.savings_account;

import faang.school.accountservice.dto.account.AccountResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountResponseDto {
    private UUID id;
    private double currentRate;
    private LocalDate lastCalculationDate;
    private AccountResponseDto account;
    private BigDecimal amount;
}
