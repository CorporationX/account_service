package faang.school.accountservice.repository;

import faang.school.accountservice.jpa.AccountJpaRepository;
import faang.school.accountservice.model.Account;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final AccountJpaRepository accountJpaRepository;

    public Account findByNumber(String number) {
        return accountJpaRepository.findByNumber(number)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account: %s not found", number)));
    }

    public Account findById(Long id) {
        return accountJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account with id %s not found", id)));
    }
}
