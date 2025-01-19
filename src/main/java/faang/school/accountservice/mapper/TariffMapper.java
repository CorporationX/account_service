package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings.TariffDto;
import faang.school.accountservice.dto.savings.TariffRateHistoryDto;
import faang.school.accountservice.model.savings.Tariff;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "rateHistory", source = "rate", qualifiedByName = "initRateHistory")
  Tariff toNewEntity(TariffDto dto);

  @Named("initRateHistory")
  default String map(BigDecimal rate) {
    return "[" + rate.toString() + "%]";
  }

  Tariff toEntity(TariffRateHistoryDto dto);

  TariffRateHistoryDto toRateHistoryDto(Tariff tariff);

  @Mapping(target = "rate", source = "rateHistory", qualifiedByName = "currentRate")
  TariffDto toDto(Tariff tariff);

  @Named("currentRate")
  default BigDecimal map(String rateHistory) {
    String [] str = rateHistory.split(",");
    int index = str.length - 1;
    return new BigDecimal(str[index].trim().replaceAll("[\\[%\\]]", ""));
  }
}
