package faang.school.accountservice.validator.balance;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceValidator {
    private final BalanceRepository balanceRepository;

    public void updateBalanceValidator(Long id) {
        Balance balance = balanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found"));
    }
}
