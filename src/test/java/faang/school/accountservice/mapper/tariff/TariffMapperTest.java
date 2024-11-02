package faang.school.accountservice.mapper.tariff;

import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.rate.RateMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TariffMapperTest {

    private final TariffMapperImpl tariffMapper = new TariffMapperImpl(new RateMapperImpl());

    private static final Long ID = 1L;
    private static final String TARIFF_TYPE = "type";
    private static final Double INTEREST_RATE = 5.0;
    private static final String RATE_HISTORY = "{5.0}";
    private Tariff tariff;
    private List<Tariff> tariffs = new ArrayList<>();

    @BeforeEach
    public void init() {
        tariff = Tariff.builder()
                .tariffName(TARIFF_TYPE)
                .rate(Rate.builder()
                        .id(ID)
                        .interestRate(INTEREST_RATE)
                        .build())
                .rateHistory(RATE_HISTORY)
                .build();

        tariffs.add(tariff);
    }

    @Test
    @DisplayName("Успех при маппинге Tariff в TariffDto")
    public void testToDto() {
        TariffDto resultTariffDto = tariffMapper.toDto(tariff);

        assertNotNull(resultTariffDto);
        assertEquals(tariff.getTariffName(), resultTariffDto.getTariffName());

        assertNotNull(resultTariffDto.getRateDto());
        assertEquals(tariff.getRate().getId(), resultTariffDto.getRateDto().getId());
        assertEquals(tariff.getRate().getInterestRate(), resultTariffDto.getRateDto().getInterestRate());
        assertEquals(tariff.getRateHistory(), resultTariffDto.getRateHistory());
    }

    @Test
    @DisplayName("Успех при маппинге списка Tariff в список TariffDto")
    public void testToDtos() {
        List<TariffDto> resultTariffDtos = tariffMapper.toDtos(tariffs);

        assertNotNull(resultTariffDtos);
        assertEquals(tariffs.size(), resultTariffDtos.size());
        assertEquals(tariffs.get(0).getTariffName(), resultTariffDtos.get(0).getTariffName());

        assertNotNull(resultTariffDtos.get(0).getRateDto());
        assertEquals(tariffs.get(0).getRate().getId(), resultTariffDtos.get(0).getRateDto().getId());
        assertEquals(tariffs.get(0).getRate().getInterestRate(), resultTariffDtos.get(0).getRateDto().getInterestRate());
        assertEquals(tariffs.get(0).getRateHistory(), resultTariffDtos.get(0).getRateHistory());
    }
}