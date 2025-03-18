package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.RateChangeRequestDto;
import faang.school.accountservice.entity.RateChangeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RateChangeRequestMapper {

    @Mapping(source = "tariff.id", target = "tariffId")
    RateChangeRequestDto toDto(RateChangeRequest changeRequest);

    RateChangeRequest toEntity(RateChangeRequestDto changeRequestDto);
}
