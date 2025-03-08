package faang.school.accountservice.controller;

import faang.school.accountservice.dto.cashback.CashbackRuleCreateDto;
import faang.school.accountservice.dto.cashback.CashbackRuleReadDto;
import faang.school.accountservice.service.cashback.CashbackRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/cashback-rules")
@RestController
@RequiredArgsConstructor
public class CashbackRuleController {
    private final CashbackRuleService cashbackRuleService;

    @PostMapping
    public CashbackRuleReadDto createCashbackRule(@RequestBody @Valid CashbackRuleCreateDto dto) {
        return cashbackRuleService.createRule(dto);
    }
}
