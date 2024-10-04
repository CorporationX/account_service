package faang.school.accountservice.mapper;

import faang.school.accountservice.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class TariffRateMapperTest {
    private final TariffAndRateMapper mapper = Mappers.getMapper(TariffAndRateMapper.class);

    @Test
    void mapToDto() {
        // given - precondition
        var savingsAccount = TestDataFactory.createSavingsAccount();
        var expectedResult = TestDataFactory.createTariffAndRateDto();

        // when - action
        var actualResult = mapper.mapToDto(savingsAccount);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}