package faang.school.accountservice.service;

import faang.school.accountservice.dto.CreateTariffRequest;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.UpdateTariffRequest;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.TariffRateHistory;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @InjectMocks
    private TariffService tariffService;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffMapper tariffMapper;

    private Long tariffId;
    private String tariffName;
    private Tariff tariff;

    @BeforeEach
    void setUp(){
        tariffId = 1L;
        tariffName = "Test Tariff";
        tariff  = Tariff.builder().build();
    }

    @Test
    void shouldCreateTariff() {
        BigDecimal initialRate = BigDecimal.valueOf(0.05);
        CreateTariffRequest request = CreateTariffRequest.builder()
                .name(tariffName)
                .initialRate(initialRate)
                .build();
        Tariff savedTariff = Tariff.builder().name(tariffName).build();
        TariffRateHistory initialRateHistory = TariffRateHistory.builder()
                .tariff(savedTariff)
                .rate(initialRate)
                .build();
        savedTariff.getRateHistory().add(initialRateHistory);
        TariffDto expectedTariffDto = TariffDto.builder()
                .name(tariffName)
                .currentRate(initialRate)
                .build();
        when(tariffRepository.save(any(Tariff.class))).thenReturn(savedTariff);
        when(tariffMapper.toDto(savedTariff)).thenReturn(expectedTariffDto);
        TariffDto createdTariffDto = tariffService.createTariff(request);
        assertEquals(expectedTariffDto, createdTariffDto);
    }

    @Test
    void shouldUpdateTariff() {
        BigDecimal newRate = BigDecimal.valueOf(0.06);
        UpdateTariffRequest request = UpdateTariffRequest.builder()
                .newRate(newRate)
                .build();
        Tariff existingTariff = Tariff.builder().id(tariffId).build();
        Tariff updatedTariff = Tariff.builder().id(tariffId).rateHistory(existingTariff.getRateHistory()).build();
        TariffRateHistory newRateHistory = TariffRateHistory.builder()
                .tariff(updatedTariff)
                .rate(newRate)
                .build();
        updatedTariff.getRateHistory().add(newRateHistory);
        TariffDto expectedTariffDto = TariffDto.builder()
                .id(tariffId)
                .currentRate(newRate)
                .build();
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(existingTariff));
        when(tariffRepository.save(updatedTariff)).thenReturn(updatedTariff);
        when(tariffMapper.toDto(updatedTariff)).thenReturn(expectedTariffDto);
        TariffDto updatedTariffDto = tariffService.updateTariff(tariffId, request);
        assertEquals(expectedTariffDto, updatedTariffDto);
    }

    @Test
    void shouldGetTariff() {
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        Tariff result = tariffService.getTariff(tariffId);
        assertEquals(tariff, result);
    }

    @Test
    void shouldGetTariffById() {
        TariffDto tariffDto = TariffDto.builder().build();
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        when(tariffMapper.toDto(any(Tariff.class))).thenReturn(tariffDto);
        TariffDto result = tariffService.getTariffById(tariffId);
        assertEquals(tariffDto, result);
    }
}