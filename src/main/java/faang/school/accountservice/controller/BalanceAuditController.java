package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceAuditDto;
import faang.school.accountservice.dto.balance.RequestBalanceAuditDto;
import faang.school.accountservice.service.BalanceAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit")
public class BalanceAuditController {

    private final BalanceAuditService balanceAuditService;

    @PostMapping
    public BalanceAuditDto createAudit(@RequestBody RequestBalanceAuditDto requestDto){
        return balanceAuditService.createAudit(requestDto);
    }
}
