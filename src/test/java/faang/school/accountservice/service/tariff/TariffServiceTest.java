package faang.school.accountservice.service.tariff;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.HistoryDto;
import faang.school.accountservice.dto.tariff.TariffCreateDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.tariff.TariffMapperImpl;
import faang.school.accountservice.repository.tariff.TariffRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {
    @Mock
    private TariffRepository tariffRepository;
    @Spy
    private TariffMapperImpl tariffMapper;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private TariffService tariffService;

    @Test
    void testFindEntityById() throws Exception {
        final Long id = 1L;
        final Tariff mockTariff = Tariff.builder()
                .id(id)
                .rate(BigDecimal.valueOf(0.16))
                .name("PROMO")
                .rateHistory(provideTestHistory())
                .build();

        Mockito.when(tariffRepository.findById(id))
                .thenReturn(Optional.of(mockTariff));

        Tariff tariff = tariffService.findEntityById(1L);

        assertAll(
                () -> assertEquals(id, tariff.getId()),
                () -> assertEquals(BigDecimal.valueOf(0.16), tariff.getRate()),
                () -> assertEquals("PROMO", tariff.getName())
        );
    }

    @Test
    void testFindEntityByIdNotFound() {
        Mockito.when(tariffRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> tariffService.findEntityById(1L));
    }

    @Test
    void testFindById() throws Exception {
        final Long id = 1L;
        final Tariff mockTariff = Tariff.builder()
                .id(id)
                .rate(BigDecimal.valueOf(0.16))
                .name("PROMO")
                .rateHistory(provideTestHistory())
                .build();

        Mockito.when(tariffRepository.findById(id))
                .thenReturn(Optional.of(mockTariff));

        TariffDto tariffDto = tariffService.findById(id);

        assertAll(
                () -> assertEquals(id, tariffDto.getId()),
                () -> assertEquals(BigDecimal.valueOf(0.16), tariffDto.getRate()),
                () -> assertEquals("PROMO", tariffDto.getName())
        );
    }

    @Test
    void testFindByIdNotFound() {
        Mockito.when(tariffRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> tariffService.findById(1L));
    }

    @Test
    void testCreateTariff() throws Exception {
        final Long id = 1L;
        final BigDecimal rate = BigDecimal.valueOf(0.16);
        TariffCreateDto createDto = TariffCreateDto.builder()
                .name("TARIFF_1")
                .rate(rate)
                .build();
        final Tariff mockTariff = provideTariff(id, rate);
        Mockito.when(tariffRepository.save(any()))
                .thenReturn(mockTariff);

        TariffDto tariffDto = tariffService.createTariff(createDto);

        assertAll(
                () -> assertEquals(id, tariffDto.getId()),
                () -> assertEquals("TARIFF_1", tariffDto.getName()),
                () -> assertEquals(BigDecimal.valueOf(0.16), tariffDto.getRate()),
                () -> assertEquals(mockTariff.getRateHistory(), tariffDto.getRateHistory())
        );
    }

    @Test
    void testUpdateTariff() throws Exception {
        final Long id = 1L;

        TariffUpdateDto updateDto = TariffUpdateDto.builder()
                .id(id)
                .rate(BigDecimal.valueOf(0.18))
                .build();
        String historyRaw = provideTestHistory();
        final Tariff mockTariff = Tariff.builder()
                .id(id)
                .rate(BigDecimal.valueOf(0.16))
                .name("PROMO")
                .rateHistory(historyRaw)
                .build();

        Mockito.when(tariffRepository.findById(id))
                .thenReturn(Optional.of(mockTariff));

        TariffDto tariffDto = tariffService.updateTariff(updateDto);

        int expectedHistorySize = objectMapper.readValue(historyRaw, HistoryDto[].class).length + 1;
        int historySize = objectMapper.readValue(tariffDto.getRateHistory(), HistoryDto[].class).length;
        assertAll(
                () -> assertEquals(1L, tariffDto.getId()),
                () -> assertEquals("PROMO", tariffDto.getName()),
                () -> assertEquals(BigDecimal.valueOf(0.18), tariffDto.getRate()),
                () -> assertEquals(expectedHistorySize, historySize)
        );
    }

    private Tariff provideTariff(Long id, BigDecimal rate) throws Exception {
        return Tariff.builder()
                .id(id)
                .name("TARIFF_" + id)
                .rate(rate)
                .rateHistory(provideTestHistory())
                .build();
    }

    private String provideTestHistory() throws Exception {
        List<HistoryDto> history = List.of(
                new HistoryDto(null, "0.16", LocalDateTime.now().minusDays(2)),
                new HistoryDto("0.16", "0.16", LocalDateTime.now().minusDays(1))
        );
        return objectMapper.writeValueAsString(history);
    }
}
