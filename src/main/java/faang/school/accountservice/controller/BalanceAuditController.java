package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.audit.BalanceAuditDto;
import faang.school.accountservice.service.BalanceAuditService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/balance-audit")
@RequiredArgsConstructor
public class BalanceAuditController {
    private final BalanceAuditService balanceAuditService;

    @GetMapping("/balanceAuditId/{balanceAuditId}")
    @ResponseStatus(HttpStatus.OK)
    public BalanceAuditDto getBalanceAudit(@PathVariable("balanceAuditId") @Positive Long balanceAuditId) {
        return balanceAuditService.getBalanceAudit(balanceAuditId);
    }

    @GetMapping
    public List<BalanceAuditDto> getBalanceAudits() {
        return balanceAuditService.getAllBalanceAudit();
    }
}
