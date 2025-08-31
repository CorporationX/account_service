package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.AccountBalanceDto;
import faang.school.accountservice.model.dto.BalanceAuditDto;
import faang.school.accountservice.service.AccountBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountServiceController {

    private final AccountBalanceService service;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountBalanceDto> getBalance(@PathVariable Long accountId) {
        var balance = service.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{accountId}/audit")
    public ResponseEntity<List<BalanceAuditDto>> getAudit(@PathVariable Long accountId) {
        var audit = service.getAudit(accountId);
        return ResponseEntity.ok(audit);
    }
}