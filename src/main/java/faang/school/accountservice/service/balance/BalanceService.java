package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.balance.BalanceNotFoundException;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private BalanceRepository balanceRepository;

    @Transactional(readOnly = true)
    public Balance getBalanceById(UUID balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> {
                    log.error("Balance with id {} not found", balanceId);
                    return new BalanceNotFoundException(balanceId);
                });
    }
}
