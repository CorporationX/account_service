package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceJpaRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper mapper;
    private final BalanceAuditMapper auditMapper;

    @Override
    public void create(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto, accountRepository.getReferenceById(balanceDto.getAccountId()));
        balance.setVersion(1);

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    @Override
    public void update(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto, accountRepository.getReferenceById(balanceDto.getAccountId()));
        balance.setUpdatedAt(LocalDateTime.now());

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    @Override
    public void updateBalanceWithoutBalanceAudit(long balanceId, BigDecimal newBalance) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Balance %d not found".formatted(balanceId)));

        balance.setCurAuthBalance(newBalance);

        balanceRepository.save(balance);
    }

    @Override
    public BalanceDto getBalance(long accountId) {
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found"));
        Account accountReference = accountRepository.getReferenceById(accountId);
        return mapper.toDto(balance, accountReference);
    }
}
