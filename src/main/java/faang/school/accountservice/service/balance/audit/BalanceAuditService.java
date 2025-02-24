package faang.school.accountservice.service.balance.audit;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.PaymentDto;

public interface BalanceAuditService {
    void addAuditFromNewBalance(BalanceDto balanceDto);

    void addAuditFromExistingBalance(BalanceDto balanceDto, PaymentDto paymentDto);
}
