package faang.school.accountservice.adapter;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountRepositoryAdapter {
    private final AccountRepository accountRepository;

    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Account with % id not found", id)));
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
