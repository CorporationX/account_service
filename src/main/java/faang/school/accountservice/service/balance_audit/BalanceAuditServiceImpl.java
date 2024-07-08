package faang.school.accountservice.service.balance_audit;

import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.dto.balance_audit.BalanceAuditDto;
import faang.school.accountservice.dto.balance_audit.BalanceAuditFilterDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.balance_audit.filter.service.BalanceAuditFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceAuditServiceImpl implements BalanceAuditService {

    private final BalanceAuditMapper balanceAuditMapper;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditFilterService balanceAuditFilterService;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void createNewAudit(BalanceUpdateDto balanceUpdateDto) {
        Account account = accountRepository.findById(balanceUpdateDto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        BalanceAudit audit = balanceAuditMapper.toAudit(balanceUpdateDto);
        audit.setAccount(account);

        long lastVersion = balanceAuditRepository.getLastVersionByAccountId(balanceUpdateDto.getAccountId());
        audit.setVersion(lastVersion + 1);

        balanceAuditRepository.save(audit);
        log.info("Created new audit: {}", audit.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BalanceAuditDto> findByAccountId(long accountId, BalanceAuditFilterDto balanceAuditFilterDto) {

        Stream<BalanceAudit> balanceAuditStream = balanceAuditRepository.findAllByAccountId(accountId).stream();

        return balanceAuditFilterService.acceptAll(balanceAuditStream, balanceAuditFilterDto)
                .sorted(Comparator.comparing(BalanceAudit::getCreatedAt))
                .map(balanceAuditMapper::toDto)
                .toList();
    }
}
