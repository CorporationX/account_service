package faang.school.accountservice.mapper.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.model.account.Tariff;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TariffMapper {

    @Setter(onMethod_ = @Autowired)
    private ObjectMapper objectMapper;

    @Mapping(target = "currentRate", source = "rateHistory", qualifiedByName = "extractLastRate")
    public abstract TariffDto toDto(Tariff tariff);

    @Mapping(target = "rateHistory", source = "currentRate", qualifiedByName = "initializeRateHistory")
    public abstract Tariff toEntity(TariffDto tariffDTO);

    public void update(TariffDto tariffDto, @MappingTarget Tariff tariff) {
        tariff.setName(tariffDto.name());
        var rateHistory = new ArrayList<>(stringToDoubleList(tariff.getRateHistory()));
        rateHistory.add(tariffDto.currentRate());
        tariff.setRateHistory(doubleListToString(rateHistory));
    }

    @Named("extractLastRate")
    public double extractLastRate(String rateHistory) {
        if (rateHistory == null) {
            return 0.0;
        }
        try {
            var rates = stringToDoubleList(rateHistory);
            return rates.isEmpty() ? 0.0 : rates.get(rates.size() - 1);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Named("initializeRateHistory")
    protected String initializeRateHistory(Double currentRate) {
        return "[" + currentRate + "]";
    }

    protected List<Double> stringToDoubleList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error parsing rate history", e);
        }
    }

    protected String doubleListToString(List<Double> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing rate history", e);
        }
    }
}
