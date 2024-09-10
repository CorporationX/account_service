package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.model.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evgenii Malkov
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {

    @Mapping(source = "percentTariffHistory", target = "percent", qualifiedByName = "actualTariffPercent")
    TariffDto toDto(Tariff tariff);

    List<TariffDto> toListDto(List<Tariff> tariffList);

    @Named("actualTariffPercent")
    static BigDecimal actualTariffPercent(List<BigDecimal> percents) {
        return percents.get(percents.size() - 1);
    }
}
