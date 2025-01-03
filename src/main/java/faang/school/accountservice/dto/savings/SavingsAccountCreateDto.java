package faang.school.accountservice.dto.savings;

import faang.school.accountservice.dto.account.AccountDtoOpen;
import lombok.Builder;

@Builder
public record SavingsAccountCreateDto(
    AccountDtoOpen account,
    Long tariffId
) {

}
