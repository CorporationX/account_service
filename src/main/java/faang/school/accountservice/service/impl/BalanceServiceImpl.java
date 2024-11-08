package faang.school.accountservice.service.impl;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import faang.school.accountservice.service.BalanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceJpaRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceMapper mapper;
    private final BalanceAuditMapper auditMapper;

    public void create(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setVersion(1);

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    public void update(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setUpdatedAt(LocalDateTime.now());

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    public BalanceDto getBalance(long accountId) {
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found"));
        return mapper.toDto(balance);
    }
}
