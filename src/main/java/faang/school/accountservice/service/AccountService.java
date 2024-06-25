package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountValidator accountValidator;
    private final AccountRepository accountRepository;
    private final BalanceService balanceService; // TODO: возможно нужно будет платежного счета создавать баланс

    public AccountDto openAccount(AccountDto accountDto) {
        accountValidator.validateOwner(accountDto);
        return
    }

}