package faang.school.accountservice.service.cashback;

import faang.school.accountservice.dto.cashback.CashbackPlanCreateDto;
import faang.school.accountservice.dto.cashback.CashbackPlanReadDto;
import faang.school.accountservice.entity.cashback.CashbackPlan;
import faang.school.accountservice.entity.cashback.CashbackRule;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.CashbackPlanMapper;
import faang.school.accountservice.repository.CashbackPlanRepository;
import faang.school.accountservice.validator.cashback.CashbackPlanValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CashbackPlanService {
    private final CashbackPlanRepository cashbackPlanRepository;
    private final CashbackPlanMapper cashbackPlanMapper;
    private final CashbackPlanValidator cashbackPlanValidator;

    @Transactional
    public CashbackPlanReadDto createPlan(CashbackPlanCreateDto dto) {
        var rulesIds = dto.getRulesIds();
        cashbackPlanValidator.validateOnAllRulesExist(rulesIds);
        CashbackPlan cashbackPlan = cashbackPlanMapper.toEntity(dto);

        cashbackPlan = cashbackPlanRepository.save(cashbackPlan);
        cashbackPlanRepository.addRulesToPlan(
                cashbackPlan.getId(), rulesIds
        );
        return cashbackPlanMapper.toDto(cashbackPlan, rulesIds);
    }

    @Transactional
    public CashbackPlanReadDto addCashbackRulesToPlan(List<Long> rulesIds, Long planId) {
        var cashbackPlan = getPlanById(planId);
        var existingRulesIds = cashbackPlan.getRules().stream()
                .map(CashbackRule::getId)
                .toList();
        cashbackPlanValidator.validateAddCashbackRulesToPlan(rulesIds, existingRulesIds);
        cashbackPlanRepository.addRulesToPlan(
                planId, rulesIds
        );
        var allPlanRulesIds = Stream.concat(existingRulesIds.stream(), rulesIds.stream())
                .toList();
        return cashbackPlanMapper.toDto(cashbackPlan, allPlanRulesIds);
    }


    public CashbackPlanReadDto getPlan(Long id) {
        var cashbackPlan = getPlanById(id);
        return cashbackPlanMapper.toDto(cashbackPlan);
    }

    private CashbackPlan getPlanById(Long id) {
        return cashbackPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("План начисления кэшбэка не найден"));
    }
}
