package faang.school.accountservice.service;

import faang.school.accountservice.adapter.AccountRepositoryAdapter;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepositoryAdapter accountRepositoryAdapter;

    @Override
    @Transactional
    public Account createAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        account.setAccountStatus(AccountStatus.ACTIVE);
        return accountRepositoryAdapter.save(account);
    }
//TODO идепотентность
    @Override
    @Transactional
    public Account blockAccount(Long id) {
        Account account = accountRepositoryAdapter.findById(id);
        account.setAccountStatus(AccountStatus.FROZEN);
        int version = account.getVersion();
        account.setVersion(version++);
        return accountRepositoryAdapter.save(account);
    }

    @Override
    @Transactional
    public Account closeAccount(Long id) {
        Account account = accountRepositoryAdapter.findById(id);
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        int version = account.getVersion();
        account.setVersion(version++);
        return accountRepositoryAdapter.save(account);
    }
}
