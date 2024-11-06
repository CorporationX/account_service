package faang.school.accountservice.mapper.rate;

import faang.school.accountservice.dto.rate.RateDto;
import faang.school.accountservice.entity.rate.Rate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class RateMapperTest {

    private final RateMapper rateMapper = Mappers.getMapper(RateMapper.class);

    private static final Long ID = 1L;
    private static final Double INTEREST_RATE = 5.0;

    @Test
    @DisplayName("Успех при маппинге Rate в RateDto")
    public void testToDto() {
        Rate rate = Rate.builder()
                .id(ID)
                .interestRate(INTEREST_RATE)
                .build();

        RateDto resultRateDto = rateMapper.toDto(rate);

        assertNotNull(resultRateDto);
        assertEquals(rate.getId(), resultRateDto.getId());
        assertEquals(rate.getInterestRate(), resultRateDto.getInterestRate());
    }
}