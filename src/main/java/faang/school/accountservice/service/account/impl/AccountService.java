package faang.school.accountservice.service.account.impl;

import faang.school.accountservice.dto.RequestAccountDto;
import faang.school.accountservice.dto.ResponseAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.enums.Status;
import faang.school.accountservice.exception.InvalidAccountStateException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountAction;
import faang.school.accountservice.service.account.AccountNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;

    public ResponseAccountDto get(long accountId) {
        return accountMapper.toDto(findById(accountId));
    }

    @Transactional
    public void open(RequestAccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        account.setNumber(accountNumberGenerator.generateUniqueNumber());
        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);
        log.info("Account opened successfully");
    }

    @Transactional
    public void applyAccountAction(long accountId, AccountAction action){
        Account account = findById(accountId);
        String statusText = action.name().toLowerCase();
        if(account.getStatus().equals(action.getStatus())){
            log.warn("The operation cannot be performed: account with ID {} already {}",
                    accountId, statusText);
            throw new InvalidAccountStateException(
                    "Account with id: " + accountId + " already " + statusText
            );
        }
        account.setStatus(action.getStatus());

        if (action.getStatus().equals(Status.CLOSED)) {
            account.setClosedAt(LocalDateTime.now());
        }
        log.info("Account with id: {} success {}", accountId,statusText);
    }

    private Account findById(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account with id "+ accountId +" does not exists"));
    }
}