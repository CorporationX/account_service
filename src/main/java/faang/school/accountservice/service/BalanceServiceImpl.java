package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceJpaRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceMapper mapper;
    private final BalanceAuditMapper auditMapper;

    @Override
    public void create(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setVersion(1);

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    @Override
    public void update(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setUpdatedAt(LocalDateTime.now());

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    @Override
    public BalanceDto getBalance(long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Not found account with id = " + accountId));
    }

    @Override
    public BalanceDto getBalanceDtoByAccountId(long accountId) {
        Balance balance = getBalanceByAccountId(accountId);
        return mapper.toDto(balance);
    }

    @Override
    public Balance getBalanceByAccountId(long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance with account id %d not found".formatted(accountId)));
    }
}
