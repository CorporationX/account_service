package faang.school.accountservice.service.account.impl;

import faang.school.accountservice.dto.RequestAccountDto;
import faang.school.accountservice.dto.ResponseAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.enums.Status;
import faang.school.accountservice.exception.InvalidAccountStateException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;

    public ResponseAccountDto get(long accountId) {
        return accountMapper.toDto(findById(accountId));
    }

    public void open(RequestAccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        account.setNumber(accountNumberGenerator.generateUniqueNumber());
        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    public void block(long accountId) {
        Account account = findById(accountId);
        if(account.getStatus().equals(Status.BLOCKED)){
            throw new InvalidAccountStateException("Account is already blocked");
        }
        account.setStatus(Status.BLOCKED);
    }

    @Transactional
    public void unblock(long accountId) {
        Account account = findById(accountId);
        if(account.getStatus().equals(Status.ACTIVE)){
            throw new InvalidAccountStateException("Account is not blocked");
        }
        account.setStatus(Status.ACTIVE);
    }

    @Transactional
    public void close(long accountId){
        Account account = findById(accountId);
        if(account.getStatus().equals(Status.CLOSED)){
            throw new InvalidAccountStateException("Account is already closed");
        }
        account.setStatus(Status.CLOSED);
        account.setClosedAt(LocalDateTime.now());
    }

    private Account findById(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account does not exists"));
    }
}