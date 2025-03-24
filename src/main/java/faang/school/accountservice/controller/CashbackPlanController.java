package faang.school.accountservice.controller;

import faang.school.accountservice.dto.cashback.CashbackPlanCreateDto;
import faang.school.accountservice.dto.cashback.CashbackPlanReadDto;
import faang.school.accountservice.service.cashback.CashbackPlanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/cashback-plans")
@RequiredArgsConstructor
public class CashbackPlanController {

    private final CashbackPlanService cashbackPlanService;

    @PostMapping
    public CashbackPlanReadDto createPlan(@RequestBody @Valid CashbackPlanCreateDto dto) {
        return cashbackPlanService.createPlan(dto);
    }

    @GetMapping("/{cashbackPlanId}")
    public CashbackPlanReadDto getPlan(
            @PathVariable
            @Valid
            @Positive
            Long cashbackPlanId
    ) {
        return cashbackPlanService.getPlan(cashbackPlanId);
    }

    @PatchMapping("/{cashbackPlanId}/rules")
    public CashbackPlanReadDto addCashbackRulesToPlan(
            @PathVariable
            @Valid
            @Positive
            Long cashbackPlanId,
            @RequestBody
            @Valid
            @NotEmpty
            List<@Positive Long> rulesIds
    ) {
        return cashbackPlanService.addCashbackRulesToPlan(rulesIds, cashbackPlanId);
    }
}
