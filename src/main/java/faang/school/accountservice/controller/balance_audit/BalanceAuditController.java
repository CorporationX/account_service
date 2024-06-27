package faang.school.accountservice.controller.balance_audit;

import faang.school.accountservice.config.context.account.AccountContext;
import faang.school.accountservice.dto.balance_audit.BalanceAuditDto;
import faang.school.accountservice.dto.balance_audit.BalanceAuditFilterDto;
import faang.school.accountservice.service.balance_audit.BalanceAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balance_audits")
public class BalanceAuditController {

    private final BalanceAuditService balanceAuditService;
    private final AccountContext accountContext;

    @GetMapping()
    public List<BalanceAuditDto> findAll(BalanceAuditFilterDto balanceAuditFilterDto) {

        long accountId = accountContext.getAccountId();
        return balanceAuditService.findByAccountId(accountId, balanceAuditFilterDto);
    }
}
