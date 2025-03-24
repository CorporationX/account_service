package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.cashback.CashbackRuleCreateDto;
import faang.school.accountservice.dto.cashback.CashbackRuleReadDto;
import faang.school.accountservice.entity.cashback.CashbackRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CashbackRuleMapper {

    @Mapping(target = "merchantId", source = "merchant.id")
    @Mapping(target = "merchantType", source = "merchant.merchantType")
    CashbackRuleReadDto toDto(CashbackRule rule);

    @Mapping(target = "merchant", ignore = true)
    CashbackRule toEntity(CashbackRuleCreateDto dto);
}
