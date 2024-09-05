package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.exeption.NotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountUtilService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public Account getById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Account with ID = " + id + " not found"));
        log.info("Account with ID = {} found", id);
        return account;
    }

    public AccountDto changeAccountStatus(Long id, String status) {
        Account account = getById(id);
        if (account.getStatus().equals(Status.CLOSED) && status.equalsIgnoreCase(Status.ACTIVE.name())) {
            log.warn("Account with ID = {} is CLOSE. It cannot have ACTIVE status again", id);
        } else if (account.getStatus().name().equalsIgnoreCase(status)) {
            log.warn("Account with ID = {} already has status: {}", id, status);
        } else {
            account.setStatus(Status.valueOf(status));
            if (status.equalsIgnoreCase(Status.CLOSED.name())) {
                account.setClosedAt(LocalDateTime.now());
            }
            accountRepository.save(account);
            log.info("Account with ID = {} has status: {}", id, account.getStatus().name());
        }
        return accountMapper.toDto(account);
    }
}
