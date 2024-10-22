package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDto getAccount(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        return accountMapper.accountToAccountDto(account);
    }

    @Override
    public AccountDto openAccount(AccountDto accountDto) {
        Account account = accountMapper.accountDtoToAccount(accountDto);

        return null;
    }
}
