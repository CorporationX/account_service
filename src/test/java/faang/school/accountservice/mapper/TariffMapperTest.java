package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRateHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TariffMapperTest {
    @InjectMocks
    private TariffMapperImpl tariffMapper;

    @Mock
    private Tariff tariff;

    @Mock
    private TariffRateHistory tariffRateHistory;

    @Test
    void shouldMapTariffToDto() {
        Long tariffId = 1L;
        String tariffName = "Tariff Name";
        BigDecimal currentRate = BigDecimal.valueOf(0.05);
        List<TariffRateHistory> rateHistoryList = new ArrayList<>();
        rateHistoryList.add(tariffRateHistory);
        when(tariff.getId()).thenReturn(tariffId);
        when(tariff.getName()).thenReturn(tariffName);
        when(tariff.getRateHistory()).thenReturn(rateHistoryList);
        when(tariffRateHistory.getRate()).thenReturn(currentRate);
        TariffDto result = tariffMapper.toDto(tariff);
        assertEquals(tariffId, result.getId());
        assertEquals(tariffName, result.getName());
        assertEquals(currentRate, result.getCurrentRate());
    }

    @Test
    void shouldGetCurrentRateNoRateHistory() {
        when(tariff.getRateHistory()).thenReturn(new ArrayList<>());
        BigDecimal result = tariffMapper.getCurrentRate(tariff);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void shouldGetCurrentRateMultipleRateHistories() {
        BigDecimal currentRate = BigDecimal.valueOf(0.05);
        BigDecimal oldRate = BigDecimal.valueOf(0.04);

        TariffRateHistory currentRateHistory = TariffRateHistory.builder()
                .rate(currentRate)
                .createdAt(LocalDateTime.now())
                .build();

        TariffRateHistory oldRateHistory = TariffRateHistory.builder()
                .rate(oldRate)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();

        List<TariffRateHistory> rateHistoryList = List.of(oldRateHistory, currentRateHistory);

        when(tariff.getRateHistory()).thenReturn(rateHistoryList);
        BigDecimal result = tariffMapper.getCurrentRate(tariff);
        assertEquals(currentRate, result);
    }
}