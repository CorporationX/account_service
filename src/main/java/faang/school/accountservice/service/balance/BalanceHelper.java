package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.exception.BalanceOperationConflictException;
import faang.school.accountservice.mapper.BalanceMapper2;
import faang.school.accountservice.model.Balance2;
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
    private final BalanceMapper2 balanceMapper;
    private final BalanceService2 balanceService2;

    @Transactional
    public BalanceViewDto executeBalanceOperation(Long accountId, Consumer<Balance2> action) {
        try {
            Balance2 balance2 = balanceService2.getBalanceEntity(accountId);
            action.accept(balance2);
            return balanceMapper.toViewDto(balance2);
        } catch (ObjectOptimisticLockingFailureException exception) {
            log.error("Optimistic lock conflict for balance operation", exception);
            throw new BalanceOperationConflictException(
                    "Data was modified by another user. Please refresh and try again.", exception
            );
        }
    }
}