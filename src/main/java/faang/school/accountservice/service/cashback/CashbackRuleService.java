package faang.school.accountservice.service.cashback;

import faang.school.accountservice.dto.cashback.CashbackRuleCreateDto;
import faang.school.accountservice.dto.cashback.CashbackRuleReadDto;
import faang.school.accountservice.entity.cashback.CashbackRule;
import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.mapper.CashbackRuleMapper;
import faang.school.accountservice.repository.CashbackRuleRepository;
import faang.school.accountservice.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CashbackRuleService {
    private final CashbackRuleRepository cashbackRuleRepository;
    private final CashbackRuleMapper cashbackRuleMapper;
    private final MerchantService merchantService;

    public List<CashbackRule> getAllRulesByIds(List<Long> ids) {
        return cashbackRuleRepository.findAllById(ids);
    }

    public long countByIds(List<Long> ids) {
        return cashbackRuleRepository.countByIdIn(ids);
    }

    public CashbackRuleReadDto createRule(CashbackRuleCreateDto createDto) {
        var cashbackRule = cashbackRuleMapper.toEntity(createDto);
        if (createDto.getMerchantId() != null) {
            var merchant = merchantService.findMerchantById(createDto.getMerchantId());
            cashbackRule.setMerchant(merchant);
        }
        try {
            return cashbackRuleMapper.toDto(cashbackRuleRepository.save(cashbackRule));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Такое правило уже существует");
        }
    }

}
