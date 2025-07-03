package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.balance.BalanceNotFoundException;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.validation.balance.BalanceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceValidator balanceValidator;

    @Transactional(readOnly = true)
    public Balance getBalanceById(UUID balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> getBalanceNotFound(balanceId));
    }

    @Transactional
    public Balance authorizeBalance(UUID balanceId, BigDecimal amount) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        BigDecimal available = balance.getBalance().subtract(balance.getAuthorizedBalance());
        balanceValidator.validateAmountDoesNotExceedLimit(balanceId, amount, available);

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));

        log.info("Authorized {} on balance {}", amount, balance);
        return balance;
    }

    @Transactional
    public Balance clearAuthorizationBalance(UUID balanceId, BigDecimal amount) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        balanceValidator.validateAmountDoesNotExceedLimit(balanceId, amount, balance.getAuthorizedBalance());

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
        balance.setBalance(balance.getBalance().subtract(amount));

        log.info("Cleared {} from balance {}", amount, balance);
        return balance;
    }

    @Transactional
    public Balance cancelAuthorizationBalance(UUID balanceId, BigDecimal amount) {
        Balance balance = getBalanceByIdForUpdate(balanceId);

        balanceValidator.validateAmountDoesNotExceedLimit(balanceId, amount, balance.getAuthorizedBalance());

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));

        log.info("Canceled authorization of {} on balance {}", amount, balance);
        return balance;
    }

    private Balance getBalanceByIdForUpdate(UUID balanceId) {
        return balanceRepository.findByIdForUpdate(balanceId)
                .orElseThrow(() -> getBalanceNotFound(balanceId));
    }

    private BalanceNotFoundException getBalanceNotFound(UUID balanceId) {
        log.error("Balance with id {} not found", balanceId);
        return new BalanceNotFoundException(balanceId);
    }
}
