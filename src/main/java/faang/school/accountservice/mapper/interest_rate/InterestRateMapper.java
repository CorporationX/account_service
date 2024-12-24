package faang.school.accountservice.mapper.interest_rate;

import faang.school.accountservice.dto.interest_rate.InterestRateDto;
import faang.school.accountservice.model.interest_rate.InterestRate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterestRateMapper {
    InterestRateDto toDto(InterestRate interestRate);
    List<InterestRateDto> toListDto(List<InterestRate> interestRates);
    InterestRate toEntity(InterestRateDto interestRateDto);
}