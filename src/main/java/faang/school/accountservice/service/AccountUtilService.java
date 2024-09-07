package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.OpenAccountDto;
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

    public Account getByNumber(String number) {
        Account account = accountRepository.findByNumber(number).orElseThrow(() ->
                new NotFoundException("Account with number = " + number + " not found"));
        log.info("Account with number = {} found", number);
        return account;
    }

    public Account getByOwner(Long ownerId, String ownerType) {
        Account account = accountRepository.findByOwner(ownerId, ownerType).orElseThrow(() ->
                new NotFoundException("Account by owner with ID = " + ownerId +
                        " and type: " + ownerType + " not found"));
        log.info("Account by owner with ID = {} and type: {} found", ownerId, ownerType);
        return account;
    }

    public Account openAccount(OpenAccountDto openAccountDto) {
        Account account = accountMapper.toEntity(openAccountDto);
        account.setStatus(Status.ACTIVE);
        Account openAccount = accountRepository.save(account);
        log.info("Account with number = {} opened", openAccountDto.getNumber());
        return openAccount;
    }

    public Account changeAccountStatus(Long id, String status) {
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
        return account;
    }
}
