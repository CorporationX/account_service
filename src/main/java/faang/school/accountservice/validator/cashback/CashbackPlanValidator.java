package faang.school.accountservice.validator.cashback;

import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.service.cashback.CashbackRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CashbackPlanValidator {
    private final CashbackRuleService cashbackRuleService;

    public void validateAddCashbackRulesToPlan(List<Long> rulesIds, List<Long> existingRuleIds) {
        validateRulesOnExistInPlan(rulesIds, existingRuleIds);
        validateOnAllRulesExist(rulesIds);
    }

    public void validateRulesOnExistInPlan(List<Long> rulesIds, List<Long> existingRuleIds) {
        existingRuleIds.stream()
                .filter(rulesIds::contains)
                .anyMatch(id -> {
                    throw new BusinessException("План уже содержит правило с ID " + id);
                });
    }

    public void validateOnAllRulesExist(List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException("У плана должно быть хотя бы одно правило");
        }
        long existCount = cashbackRuleService.countByIds(ids);
        if (existCount != ids.size()) {
            throw new BusinessException(existCount - ids.size() + " правил не существует");
        }
    }
}
