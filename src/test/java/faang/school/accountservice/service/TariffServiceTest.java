package faang.school.accountservice.service;

import faang.school.accountservice.entity.RateHistory;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.repository.RateHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.accountservice.util.TestDataFactory.ACCOUNT_NUMBER;
import static faang.school.accountservice.util.TestDataFactory.createRateHistory;
import static faang.school.accountservice.util.TestDataFactory.createRateHistoryList;
import static faang.school.accountservice.util.TestDataFactory.createTariff;
import static faang.school.accountservice.util.TestDataFactory.createTariffAndRateDto;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {
    @InjectMocks
    private TariffService tariffService;
    @Mock
    private TariffRepository tariffRepository;
    @Mock
    private RateHistoryRepository rateHistoryRepository;

    @Test
    void givenAccountNumberAndRateWhenAddTariffRateThenReturnTariffAndRateObject() {
        // given - precondition
        Double tariffRate = 5.0;
        var tariff = createTariff();
        var rateHistory = createRateHistory();
        var expectedResult = createTariffAndRateDto();

        when(tariffRepository.findTariffByAccountNumber(ACCOUNT_NUMBER)).thenReturn(of(tariff));
        when(rateHistoryRepository.save(any(RateHistory.class))).thenReturn(rateHistory);
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);

        // when - action
        var actualResult = tariffService.addTariffRate(ACCOUNT_NUMBER, tariffRate);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void givenValidAccountNumberWhenGetTariffRatesThenReturnTariffRates() {
        // given - precondition
        var tariff = createTariff();
        var rateHistoryList = createRateHistoryList();
        tariff.setRateHistoryList(rateHistoryList);

        when(tariffRepository.findTariffWithRateHistoryByAccountNumber(ACCOUNT_NUMBER)).thenReturn(of(tariff));

        // when - action
        var actualResult = tariffService.getTariffRates(ACCOUNT_NUMBER);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).containsExactlyInAnyOrderElementsOf(
                tariff.getRateHistoryList().stream()
                .map(RateHistory::getRate)
                .toList());
    }
}