package faang.school.accountservice.validator;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.common.PreConditionFailedException;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BalanceValidator {
    private final BalanceRepository balanceRepository;

    public void validateBalanceNotExist(UUID accountId) {
        if (balanceRepository.existsByAccountId(accountId)) {
            throw new PreConditionFailedException("Account already have assigned balance!");
        }
    }

    public void validateEnoughActualAmount(Balance balance, BigDecimal amount) {
        BigDecimal actualAmount = balance.getActualAmount();
        BigDecimal authorizedAmount = balance.getAuthorizedAmount();
        if (actualAmount.subtract(authorizedAmount).compareTo(amount) < 0) {
            throw new PreConditionFailedException("Not enough actual funds for authorization!");
        }
    }

    public void validateEnoughAuthorizedAmount(Balance balance, BigDecimal amount) {
        if (balance.getAuthorizedAmount().compareTo(amount) < 0) {
            throw new PreConditionFailedException("Not enough authorized funds!");
        }
    }
}