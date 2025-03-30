package faang.school.accountservice.controller;

import faang.school.accountservice.dto.audit.ResponseAuditDto;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.service.balance.AsyncAuditService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/audits")
@Validated
@RestController
public class BalanceAuditController {
    private final AsyncAuditService auditService;

    @GetMapping("/{auditId}")
    public ResponseEntity<ResponseAuditDto> getAudit(@PathVariable @Positive long auditId) {

        BalanceAudit balanceAudit = auditService.getBalanceAudit(auditId);
        ResponseAuditDto dto = BalanceAuditMapper.toDto(balanceAudit);
        return ResponseEntity.ok(dto);
    }
}
