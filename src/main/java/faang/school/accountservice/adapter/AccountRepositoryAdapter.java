package faang.school.accountservice.adapter;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

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

    public List<Account> findAll(Specification specification) {
        return accountRepository.findAll(specification);
    }

    public boolean existsByAccountNumberAndOwnerIdAndOwnerTypeAndType(String accountNumber, BigInteger ownerId,
                                                                      AccountOwnerType ownerType, AccountType type) {
        return accountRepository.existsByAccountNumberAndOwnerIdAndOwnerTypeAndType(accountNumber, ownerId, ownerType, type);
    }
}
