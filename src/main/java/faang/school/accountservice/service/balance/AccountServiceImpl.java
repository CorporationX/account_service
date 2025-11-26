package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.AccountStatusType;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl {

    public AccountResponseDto getAccount(long accountId) {
        return new AccountResponseDto(1L, AccountStatusType.ACTIVE, Currency.RUB, OwnerType.USER, AccountType.DEBIT);
    }

}