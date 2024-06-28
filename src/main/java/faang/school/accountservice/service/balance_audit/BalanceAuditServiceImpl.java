package faang.school.accountservice.service.balance_audit;

import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.dto.balance_audit.BalanceAuditDto;
import faang.school.accountservice.dto.balance_audit.BalanceAuditFilterDto;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
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

    @Override
    @Transactional
    public void createNewAudit(BalanceUpdateDto balanceUpdateDto) {

        BalanceAudit audit = balanceAuditMapper.toAudit(balanceUpdateDto);

        balanceAuditRepository.save(audit);

        log.info("Created new audit: {}", audit);
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
