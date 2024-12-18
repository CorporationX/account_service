package faang.school.accountservice.service.balanceaudit;

import faang.school.accountservice.dto.balanceaudit.BalanceAuditDto;
import faang.school.accountservice.mappers.balanceaudit.BalanceAuditMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.balanceaudit.BalanceAuditRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    public BalanceAudit createBalanceAudit(Balance balance) {
        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(balance);
        balanceAudit.setTransactionId(UUID.randomUUID().toString());

       return balanceAuditRepository.save(balanceAudit);
    }

    public BalanceAuditDto getBalanceAuditById(Long balanceAuditId) {
        BalanceAudit balanceAudit = balanceAuditRepository.findById(balanceAuditId).orElseThrow(
                () -> new EntityNotFoundException("Balance audit not found")
        );
        log.info("Balance audit found: {}", balanceAudit);
        return balanceAuditMapper.toDto(balanceAudit);
    }
}
