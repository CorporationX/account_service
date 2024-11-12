package faang.school.accountservice.mapper.rate;

import faang.school.accountservice.dto.rate.RateDto;
import faang.school.accountservice.entity.rate.Rate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RateMapper {

    RateDto toDto(Rate rate);
}
