package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import faang.school.accountservice.repository.RequestJpaRepository;
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

        balanceRepository.save(balance);
    }

    public void update(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(balance);
    }

    public BalanceDto getBalance(long accountId) {
        return mapper.toDto(balanceRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Not found account with id = " + accountId)));
    }
}
