package faang.school.accountservice.repository;

import faang.school.accountservice.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static faang.school.accountservice.util.TestDataFactory.ACCOUNT_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("/test-data-repository/savings-account_and_tariff.sql")
class TariffRepositoryTest {
    @Autowired
    private TariffRepository tariffRepository;
    @Test
    void givenAccountNumberWhenFindTariffByAccountNumberReturnTariff() {
        // given - precondition
        var expectedResult = TestDataFactory.createTariff();

        // when - action
        var actualResult = tariffRepository.findTariffByAccountNumber(ACCOUNT_NUMBER);

        // then - verify the output
        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get().getId()).isEqualTo(expectedResult.getId());
    }

    @Test
    void givenAccountNumberWhenFindTariffWithRateHistoryByAccountNumberThenReturnTariffWithRateHistory() {
        // given - precondition
        var expectedResult = TestDataFactory.createRateHistory();

        // when - action
        var actualResult = tariffRepository.findTariffByAccountNumber(ACCOUNT_NUMBER);

        // then - verify the output
        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get().getRateHistoryList().get(0).getRate()).isEqualTo(expectedResult.getRate());
    }
}