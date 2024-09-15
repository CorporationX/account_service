package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.entity.account.Tariff;
import faang.school.accountservice.mapper.account.TariffMapper;
import faang.school.accountservice.repository.account.TariffRepository;
import faang.school.accountservice.validator.TariffValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TariffServiceTest {

    @Mock
    private TariffMapper tariffMapper;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffValidator tariffValidator;

    @InjectMocks
    private TariffService tariffService;

    private TariffDto tariffDto;
    private Tariff tariff;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        tariffDto = new TariffDto();
        tariffDto.setName("Basic Tariff");
        tariffDto.setRateHistory("1");

        tariff = new Tariff();
        tariff.setName("Basic Tariff");
        tariff.setRateHistory("1");
    }

    @Test
    void createTariff_Success() {
        String rate = "2";
        when(tariffMapper.toEntity(tariffDto)).thenReturn(tariff);
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);
        when(tariffMapper.toDto(any(Tariff.class))).thenReturn(tariffDto);

        TariffDto result = tariffService.createTariff(tariffDto, rate);

        assertEquals(tariffDto, result);
        verify(tariffRepository, times(1)).save(any(Tariff.class));
    }

    @Test
    void updateTariffHistory_Success() {
        UUID tariffId = UUID.randomUUID();
        String newRate = "3";

        tariff.setRateHistory("[1, 2]");

        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        tariffDto.setRateHistory("[1,2,3]");

        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);
        when(tariffMapper.toDto(any(Tariff.class))).thenReturn(tariffDto);

        TariffDto result = tariffService.updateTariffHistory(tariffId, newRate);

        assertEquals(tariffDto, result);
        verify(tariffRepository, times(1)).findById(tariffId);
        verify(tariffRepository, times(1)).save(tariff);
    }

    @Test
    void updateTariffHistory_Failure_InvalidRate() {
        UUID tariffId = UUID.randomUUID();
        String invalidRate = "invalid";

        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));

        doThrow(new RuntimeException("Invalid rate value!")).when(tariffValidator).validateRateForUpdates(eq(invalidRate), anyString());

        assertThrows(RuntimeException.class, () -> tariffService.updateTariffHistory(tariffId, invalidRate));

        verify(tariffRepository, never()).save(any(Tariff.class));
    }


}
