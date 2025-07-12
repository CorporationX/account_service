package faang.school.accountservice.service.balance_audit;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.exception.common.DataValidationException;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class BalanceAuditService {
    private final BalanceRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;

    public Balance searchBalance(UUID accountId) {
        Optional<Balance> balanceOptional = balanceRepository.findByAccountId(accountId);
        if (balanceOptional.isEmpty()) {
            throw new DataValidationException("This account does not exist");
        }
        return balanceOptional.get();
    }

}
