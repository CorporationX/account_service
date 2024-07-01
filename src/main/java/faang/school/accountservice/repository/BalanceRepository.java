package faang.school.accountservice.repository;

import faang.school.accountservice.jpa.BalanceJpaRepository;
import faang.school.accountservice.model.Balance;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BalanceRepository {

    private final BalanceJpaRepository balanceJpaRepository;

    public Balance findById(long id) {
        return balanceJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Balance: %s not found", id)));
    }
}
