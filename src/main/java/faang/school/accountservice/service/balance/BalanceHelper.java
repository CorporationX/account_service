package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceOperationConflictException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceHelper {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    public Balance getBalance(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found for authorization. Account ID: {}", accountId);
                    return new AccountNotFoundException(
                            String.format("Account not found with ID: %d", accountId));
                });
    }

    @Transactional
    public BalanceViewDto executeBalanceOperation(Long accountId, Consumer<Balance> action) {
        try {
            Balance balance = getBalance(accountId);
            action.accept(balance);
            return balanceMapper.toViewDto(balance);
        } catch (ObjectOptimisticLockingFailureException exception) {
            log.error("Optimistic lock conflict for balance operation", exception);
            throw new BalanceOperationConflictException(
                    "Data was modified by another user. Please refresh and try again.", exception
            );
        }
    }
}