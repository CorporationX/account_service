package faang.school.accountservice.service.account;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountValidator accountValidator;

    public Account getAccountEntity(long id) {
        Account account = accountValidator.validateAccountExists(id);
        log.info("getAccountEntity by id: {}: ", id);
        return account;
    }

    public List<Account> getAllByOwnerId(long id) {
        log.info("Getting Accounts for owner with id: {}: ", id);
        return accountRepository.findAllByOwnerId(id);
    }
}