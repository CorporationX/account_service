package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.exceptions.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    @Override
    public AccountDto create(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Transactional
    @Override
    public AccountDto update(Long id, AccountDto accountDto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Account not found with id: " + id));
        accountMapper.update(account, accountDto);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Transactional(readOnly = true)
    @Override
    public AccountDto getAccount(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new DataValidationException("Account not found with id: " + id));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new DataValidationException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }
}
