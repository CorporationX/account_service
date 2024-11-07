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

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceJpaRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper mapper;
    private final BalanceAuditMapper auditMapper;

    public void create(BalanceDto balanceDto) {
        Account accountReference = accountRepository.getReferenceById(balanceDto.getAccountId());
        Balance balance = mapper.toEntity(balanceDto, accountReference);

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    @Override
    public void update(BalanceDto balanceDto) {
        Account accountReference = accountRepository.getReferenceById(balanceDto.getAccountId());
        Balance balance = mapper.toEntity(balanceDto, accountReference);

        balanceAuditRepository.save(auditMapper.toEntity(balance));
        balanceRepository.save(balance);
    }

    @Override
    public BalanceDto getBalanceDtoByAccountId(long accountId) {
        Account accountReference = accountRepository.getReferenceById(accountId);
        return balanceRepository.findByAccountId(accountId)
                .map(balance -> mapper.toDto(balance, accountReference))
                .orElseThrow(() -> new EntityNotFoundException("Balance with account id %d not found".formatted(accountId)));
    }

    @Override
    public Balance getBalanceWithAccountByAccountId(long accountId) {
        return balanceRepository.findByWithAccountAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance with account id %s not found".formatted(accountId)));
    }
}
