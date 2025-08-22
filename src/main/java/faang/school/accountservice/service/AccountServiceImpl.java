package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountUpdateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper mapper;
    private final AccountRepository repository;

    @Override
    public AccountViewDto openAccount(AccountCreateDto createDto) {
        Account createAccount = mapper.toEntity(createDto);

        Long min = 100_000_000_000L;
        Long max = 100_000_000_000_000_000L;
        Long randomNum = min + (long)(new SecureRandom().nextDouble() * (max - min + 1));

        createAccount.setAccountNumber(String.valueOf(randomNum));
        createAccount.setStatus(AccountStatus.ACTIVE);

        repository.save(createAccount);
        return mapper.toViewDto(createAccount);
    }

    @Override
    public AccountViewDto getAccount(Long id) {
        Account account = repository.findByIdOrThrow(id);
        return mapper.toViewDto(account);
    }

    @Override
    public AccountViewDto blockAccount(Long id, AccountUpdateDto updateDto) {
        Account account = repository.findByIdOrThrow(id);
        mapper.update(updateDto, account);
        Account updatedAccount = repository.save(account);
        return mapper.toViewDto(updatedAccount);
    }

    @Override
    public AccountViewDto closeAccount(Long id) {
        Account account = repository.findByIdOrThrow(id);
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        Account closedAccount = repository.save(account);
        return mapper.toViewDto(closedAccount);
    }
}
