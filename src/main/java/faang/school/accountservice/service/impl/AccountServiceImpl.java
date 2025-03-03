package faang.school.accountservice.service.impl;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountNumberGenerator;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountGenerator;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponseDto get(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("There is no account with id = %d in the database".formatted(id)));
        return accountMapper.toAccountResponseDto(account);
    }

    @Override
    public AccountResponseDto open(AccountRequestDto accountDto) {
        Account account = accountMapper.toAccountEntity(accountDto);
        account.setAccount(accountGenerator.generateUniqueAccountNumber());
        return accountMapper.toAccountResponseDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public void block(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("There is no account with id = %d in the database".formatted(id)));
        account.setAccountStatus(AccountStatus.FROZEN);
        Long accountVersion = account.getAccountVersion();
        account.setAccountVersion(++accountVersion);
    }

    @Override
    @Transactional
    public void close(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("There is no account with id = %d in the database".formatted(id)));
        account.setAccountStatus(AccountStatus.CLOSED);
        Long accountVersion = account.getAccountVersion();
        account.setAccountVersion(++accountVersion);
    }
}
