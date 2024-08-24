package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.exception.ExceptionMessage;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountValidator {
    private final AccountRepository accountRepository;

    public void checkExistenceOfTheNumber(AccountDto accountDto) {
        if (accountRepository.existsByNumber(accountDto.getNumber())) {
            log.error(ExceptionMessage.CHECK_NUMBER_EXCEPTION + accountDto.getNumber());
            throw new IllegalArgumentException(ExceptionMessage.CHECK_NUMBER_EXCEPTION + accountDto.getNumber());
        }
    }
}