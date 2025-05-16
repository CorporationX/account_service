package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.exception.BalanceOperationConflictException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
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
    private final BalanceMapper balanceMapper;
    private final BalanceService balanceService;

    @Transactional
    public BalanceViewDto executeBalanceOperation(Long accountId, Consumer<Balance> action) {
        try {
            Balance balance = balanceService.getBalanceEntity(accountId);
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