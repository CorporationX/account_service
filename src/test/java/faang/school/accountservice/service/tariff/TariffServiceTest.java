package faang.school.accountservice.service.tariff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.rate.RateDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import faang.school.accountservice.repository.tariff.TariffRepository;
import faang.school.accountservice.service.rate.RateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @InjectMocks
    private TariffService tariffService;

    @Mock
    private RateService rateService;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffMapper tariffMapper;

    @Mock
    private ObjectMapper objectMapper;

    private static final Long ID = 1L;
    private static final String TARIFF_TYPE = "type";
    private static final Double INTEREST_RATE = 5.0;
    private static final Double NEW_INTEREST_RATE = 7.1;
    private static final String RATE_HISTORY = "[5.0]";
    private static final String NEW_RATE_HISTORY = "[5.0, 7.1]";
    private List<TariffDto> tariffDtos;
    private List<Tariff> tariffs;
    private TariffRequestDto newTariffRequestDto;
    private TariffRequestDto tariffRequestDto;
    private TariffDto newTariffDto;
    private TariffDto tariffDto;
    private Tariff updatedTariff;
    private Tariff tariff;
    private Rate rate;
    private Rate newRate;

    @BeforeEach
    public void init() {
        tariffRequestDto = TariffRequestDto.builder()
                .tariffType(TARIFF_TYPE)
                .interestRate(INTEREST_RATE)
                .build();
        newTariffRequestDto = TariffRequestDto.builder()
                .tariffType(TARIFF_TYPE)
                .interestRate(NEW_INTEREST_RATE)
                .build();
        rate = Rate.builder()
                .interestRate(INTEREST_RATE)
                .build();
        newRate = Rate.builder()
                .interestRate(NEW_INTEREST_RATE)
                .build();
        tariff = Tariff.builder()
                .tariffType(TARIFF_TYPE)
                .rateHistory(RATE_HISTORY)
                .rate(rate)
                .build();
        updatedTariff = Tariff.builder()
                .tariffType(TARIFF_TYPE)
                .rateHistory(NEW_RATE_HISTORY)
                .rate(newRate)
                .build();
        tariffDto = TariffDto.builder()
                .id(ID)
                .tariffType(TARIFF_TYPE)
                .rateHistory(RATE_HISTORY)
                .rateDto(RateDto.builder()
                        .interestRate(INTEREST_RATE)
                        .build())
                .build();
        newTariffDto = TariffDto.builder()
                .id(ID)
                .tariffType(TARIFF_TYPE)
                .rateHistory(NEW_RATE_HISTORY)
                .rateDto(RateDto.builder()
                        .interestRate(NEW_INTEREST_RATE)
                        .build())
                .build();
        tariffDtos = List.of(tariffDto);
        tariffs = List.of(tariff);
    }

    @Test
    @DisplayName("Успешное создание тарифа")
    public void whenCreateTariffThenCreateTariff() {
        when(rateService.getRateByInterestRate(INTEREST_RATE)).thenReturn(rate);
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);
        when(tariffMapper.toDto(tariff)).thenReturn(tariffDto);

        TariffDto resultTariffDto = tariffService.createTariff(tariffRequestDto);

        assertNotNull(resultTariffDto);
        verify(rateService).getRateByInterestRate(INTEREST_RATE);
        verify(tariffRepository).save(any(Tariff.class));
        verify(tariffMapper).toDto(tariff);
    }

    @Test
    @DisplayName("Успех при получении списка всех тарифов")
    public void whenGetAllTariffsThenReturnListTariffDtos() {
        when(tariffRepository.findAll()).thenReturn(tariffs);
        when(tariffMapper.toDtos(tariffs)).thenReturn(tariffDtos);

        List<TariffDto> resultTariffDtos = tariffService.getAllTariffs();

        assertNotNull(resultTariffDtos);
        assertEquals(1, resultTariffDtos.size());
        verify(tariffRepository).findAll();
        verify(tariffMapper).toDtos(tariffs);
    }

    @Test
    @DisplayName("Успех при обновлении тарифной ставки")
    public void whenUpdateTariffRateShouldSuccess() throws JsonProcessingException {
        List<Double> rates = new ArrayList<>();
        rates.add(INTEREST_RATE);
        when(tariffRepository.findByTariffType(TARIFF_TYPE)).thenReturn(Optional.of(tariff));
        when(rateService.getRateByInterestRate(NEW_INTEREST_RATE)).thenReturn(newRate);
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(rates);
        when(tariffRepository.save(any(Tariff.class))).thenReturn(updatedTariff);
        when(tariffMapper.toDto(updatedTariff)).thenReturn(newTariffDto);

        TariffDto resultTariffDto = tariffService.updateTariffRate(newTariffRequestDto);

        assertNotNull(resultTariffDto);
        assertEquals(updatedTariff.getTariffType(), resultTariffDto.getTariffType());
        assertEquals(updatedTariff.getRateHistory(), resultTariffDto.getRateHistory());
        assertNotNull(resultTariffDto.getRateDto());
        assertEquals(updatedTariff.getRate().getId(), resultTariffDto.getRateDto().getId());
        assertEquals(updatedTariff.getRate().getInterestRate(), resultTariffDto.getRateDto().getInterestRate());

        verify(tariffRepository).findByTariffType(TARIFF_TYPE);
        verify(rateService).getRateByInterestRate(NEW_INTEREST_RATE);
        verify(tariffRepository).save(any(Tariff.class));
        verify(tariffMapper).toDto(updatedTariff);
    }

    @Test
    @DisplayName("Ошибка при обновлении тарифной ставки если тариф не найден")
    public void whenUpdateTariffRateThenThrowException() {
        when(tariffRepository.findByTariffType(TARIFF_TYPE)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> tariffService.updateTariffRate(tariffRequestDto));
    }
}