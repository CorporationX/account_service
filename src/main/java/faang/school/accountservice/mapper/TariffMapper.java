package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.tariff.TariffCreationDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.entity.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TariffMapper {

    @Mapping(
            target = "rateHistory",
            expression = "java(Collections.singletonList(dto.getInitRate()))"
    )
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    public Tariff toTariff(TariffCreationDto dto);

    @Mapping(target = "createdAt", dateFormat = "dd.MM.yyyy HH:mm")
    @Mapping(target = "updatedAt", dateFormat = "dd.MM.yyyy HH:mm")
    public TariffResponseDto toTariffResponseDto(Tariff tariff);

    public List<TariffResponseDto> toTariffResponseDtoList(List<Tariff> tariffs);
}
