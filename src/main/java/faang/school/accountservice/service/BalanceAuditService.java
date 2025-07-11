package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceAuditDto;
import faang.school.accountservice.dto.balance.RequestBalanceAuditDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditService {

    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;
    private final AccountService accountService;

    @Transactional
    public BalanceAuditDto createAudit(RequestBalanceAuditDto requestDto){
        Account account = accountService.getAccountById(requestDto.accountId());

        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(account.getBalance());
        balanceAudit.setOperationId(requestDto.operationId());

        balanceAuditRepository.save(balanceAudit);

        return balanceAuditMapper.toDto(balanceAudit);
    }
}
