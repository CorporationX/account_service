package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.entity.SavingsAccountRate;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.repository.SavingsAccountRateRepository;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffMapper tariffMapper;

    @Mock
    private SavingsAccountRateRepository savingsAccountRateRepository;

    @InjectMocks
    private TariffService tariffService;

    private TariffDto tariffDto;
    private Tariff tariff;
    private SavingsAccountRate savingsAccountRate;

    @BeforeEach
    void setUp() {
        tariffDto = TariffDto.builder()
                .id(1L)
                .name("STANDARD")
                .rate(BigDecimal.valueOf(5.0))
                .build();

        tariff = Tariff.builder()
                .id(1L)
                .name("STANDARD")
                .build();

        savingsAccountRate = SavingsAccountRate.builder()
                .id(1L)
                .tariff(tariff)
                .rate(BigDecimal.valueOf(5.0))
                .build();
    }

    @Test
    void testCreateTariff_Success() {

        when(tariffMapper.toEntity(tariffDto)).thenReturn(tariff);
        when(tariffRepository.save(tariff)).thenReturn(tariff);
        when(savingsAccountRateRepository.save(any(SavingsAccountRate.class))).thenReturn(savingsAccountRate);
        when(tariffMapper.toDto(tariff)).thenReturn(tariffDto);

        TariffDto result = tariffService.createTariff(tariffDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(5.0), result.getRate());

        verify(tariffMapper).toEntity(tariffDto);
        verify(tariffRepository).save(tariff);
        verify(savingsAccountRateRepository).save(any(SavingsAccountRate.class));
        verify(tariffMapper).toDto(tariff);
    }

    @Test
    void testUpdateTariff_Success() {

        BigDecimal newRate = BigDecimal.valueOf(7.5);
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));
        when(savingsAccountRateRepository.save(any(SavingsAccountRate.class)))
                .thenAnswer(invocation -> {
            SavingsAccountRate rate = invocation.getArgument(0);
            rate.setRate(newRate);
            return rate;
        });
        when(tariffMapper.toDto(tariff)).thenReturn(tariffDto);

        TariffDto result = tariffService.updateTariff(1L, newRate);

        assertNotNull(result);
        assertEquals(newRate, result.getRate());

        verify(tariffRepository).findById(1L);
        verify(savingsAccountRateRepository).save(any(SavingsAccountRate.class));
        verify(tariffMapper).toDto(tariff);
    }

    @Test
    void testUpdateTariff_TariffNotFound() {

        when(tariffRepository.findById(1L)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tariffService.updateTariff(1L, BigDecimal.valueOf(7.5)));

        assertEquals("Tariff with id 1 not found", exception.getMessage());
        verify(tariffRepository).findById(1L);
        verifyNoInteractions(savingsAccountRateRepository, tariffMapper);
    }

    @Test
    void testGetTariff_Success() {

        when(tariffRepository.findTariffDtoWithDetails(1L)).thenReturn(Optional.of(tariffDto));


        TariffDto result = tariffService.getTariff(1L);


        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(5.0), result.getRate());

        verify(tariffRepository).findTariffDtoWithDetails(1L);
    }

    @Test
    void testGetTariff_NotFound() {

        when(tariffRepository.findTariffDtoWithDetails(1L)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> tariffService.getTariff(1L));

        assertEquals("Tariff with id 1 not found", exception.getMessage());
        verify(tariffRepository).findTariffDtoWithDetails(1L);
    }

    @Test
    void testCreateTariff_VerifySavingsAccountRateCreation() {

        when(tariffMapper.toEntity(tariffDto)).thenReturn(tariff);
        when(tariffRepository.save(tariff)).thenReturn(tariff);
        when(savingsAccountRateRepository.save(any(SavingsAccountRate.class))).thenReturn(savingsAccountRate);
        when(tariffMapper.toDto(tariff)).thenReturn(tariffDto);

        tariffService.createTariff(tariffDto);

        verify(savingsAccountRateRepository).save(argThat(rate ->
                rate.getTariff().equals(tariff) &&
                        rate.getRate().equals(BigDecimal.valueOf(5.0))));
    }

    @Test
    void testUpdateTariff_VerifySavingsAccountRateUpdate() {

        BigDecimal newRate = BigDecimal.valueOf(7.5);
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));
        when(savingsAccountRateRepository.save(any(SavingsAccountRate.class))).thenAnswer(invocation -> {
            SavingsAccountRate rate = invocation.getArgument(0);
            rate.setRate(newRate);
            return rate;
        });
        when(tariffMapper.toDto(tariff)).thenReturn(tariffDto);

        tariffService.updateTariff(1L, newRate);

        verify(savingsAccountRateRepository).save(argThat(rate ->
                rate.getTariff().equals(tariff) &&
                        rate.getRate().equals(newRate)));
    }
}