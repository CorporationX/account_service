package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.cashback.CashbackPlanCreateDto;
import faang.school.accountservice.dto.cashback.CashbackPlanReadDto;
import faang.school.accountservice.entity.cashback.CashbackPlan;
import faang.school.accountservice.entity.cashback.CashbackRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CashbackRuleMapper.class}
)
public interface CashbackPlanMapper {

    @Mapping(target = "rules", ignore = true)
    CashbackPlan toEntity(CashbackPlanCreateDto dto);

    @Mapping(target = "rules", source = "rules")
    CashbackPlanReadDto toDto(CashbackPlan plan);

    @Mapping(target = "rules", source = "rulesIds")
    CashbackPlanReadDto toDto(CashbackPlan plan, List<Long> rulesIds);

    default Long mapRuleToId(CashbackRule rule) {
        return rule != null ? rule.getId() : null;
    }
}
