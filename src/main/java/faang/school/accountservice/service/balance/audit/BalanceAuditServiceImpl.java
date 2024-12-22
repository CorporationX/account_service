package faang.school.accountservice.service.balance.audit;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;
import faang.school.accountservice.mapper.balance.audit.BalanceToBalanceAuditMapper;
import faang.school.accountservice.model.balance.audit.BalanceAudit;
import faang.school.accountservice.repository.balance.audit.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class  BalanceAuditServiceImpl implements BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceToBalanceAuditMapper balanceToBalanceAuditMapper;

    @Override
    public void addAuditFromNewBalance(BalanceDto balanceDto) {
        if (balanceDto == null) {
            throw new IllegalArgumentException("balanceDto cannot be null");
        }
        BalanceAudit balanceAudit = balanceToBalanceAuditMapper.balabceToBalanceAudit(balanceDto);
        balanceAuditRepository.save(balanceAudit);
    }

    @Override
    public void addAuditFromExistingBalance(BalanceDto balanceDto, PaymentDto paymentDto) {
        if (balanceDto == null || paymentDto == null) {
            throw new IllegalArgumentException("balanceDto and paymentDto cannot be null");
        }
        BalanceAudit balanceAudit = balanceToBalanceAuditMapper.balabceToBalanceAudit(balanceDto);
        balanceAudit.setOperationId(paymentDto.paymentNumber());
        balanceAuditRepository.save(balanceAudit);
    }


}
