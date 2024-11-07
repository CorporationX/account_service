package faang.school.accountservice.service;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceAuditServiceImpl implements BalanceAuditService {

    private final BalanceAuditRepository balanceAuditRepository;

    @Override
    public BalanceAudit recordBalanceChange(BigDecimal authBalance, BigDecimal factBalance, Balance balance) {
        BalanceAudit balanceAudit = BalanceAudit.builder()
                .number(balance.getAccount().getNumber())
                .curAuthBalance(authBalance.doubleValue())
                .curFactBalance(factBalance.doubleValue())
                .balance(balance)
                .build();
        return balanceAuditRepository.save(balanceAudit);
    }
}
