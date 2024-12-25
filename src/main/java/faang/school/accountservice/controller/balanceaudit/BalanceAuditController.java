package faang.school.accountservice.controller.balanceaudit;

import faang.school.accountservice.dto.balanceaudit.BalanceAuditDto;
import faang.school.accountservice.service.balanceaudit.BalanceAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balance-audit")
@Slf4j
public class BalanceAuditController {
    private final BalanceAuditService balanceAuditService;

    @GetMapping("/{balanceAuditId}")
    public BalanceAuditDto getBalanceAuditById(@PathVariable Long balanceAuditId) {
        log.info("Received a request to get balance audit with id: {}", balanceAuditId);
        return balanceAuditService.getBalanceAuditById(balanceAuditId);
    }
}
