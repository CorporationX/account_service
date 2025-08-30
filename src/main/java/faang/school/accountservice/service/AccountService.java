package faang.school.accountservice.service;

import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.BalanceAudit;

import java.util.List;

public interface AccountService {

    void processAuthorization(PaymentMessageDto message);

    void processCancel(PaymentMessageDto message);

    void processClearing(PaymentMessageDto message);

    AccountBalance getBalance(Long accountId);

    List<BalanceAudit> getAudit(Long accountId);
}