package faang.school.accountservice.repository.account;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.jpa.AccountJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    public Account getAccountById(long id) {
        return accountJpaRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Account not found by id: " + id));
    }
}
