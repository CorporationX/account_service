package faang.school.accountservice.dto.savings;

import faang.school.accountservice.dto.account.AccountDtoResponse;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SavingsAccountResponseDto {
  private Long id;
  private AccountDtoResponse account;
  private TariffDto tariff;
  private String lastPaymentDate;
  private  Long version;
  private String createdAt;
  private String updatedAt;
}