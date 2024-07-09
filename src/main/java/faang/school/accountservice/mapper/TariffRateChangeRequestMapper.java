package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffRateChangeRequestDto;
import faang.school.accountservice.model.TariffRateChangeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TariffRateChangeRequestMapper {
    TariffRateChangeRequestDto toDto(TariffRateChangeRequest request);
}
