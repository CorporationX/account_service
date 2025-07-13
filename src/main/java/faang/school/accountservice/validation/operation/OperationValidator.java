package faang.school.accountservice.validation.operation;

import faang.school.accountservice.entity.operation.Operation;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.repository.operation.OperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OperationValidator {
    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    public void check(Operation operation) {
    }
}
