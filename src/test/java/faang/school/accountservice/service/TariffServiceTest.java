package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.RateHistory;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TariffServiceTest {
    @InjectMocks
    private TariffService tariffService;
    @Mock
    private TariffRepository tariffRepository;
    @Mock
    private TariffMapper tariffMapper;
    private Tariff tariff;
    private Tariff tariff2;
    private TariffDto tariffDto;
    private List<Double> rateHistory;

    @BeforeEach
    void setUp() {
        tariff = new Tariff();
        tariff.setId(1L);
        tariff.setName("Test tariff.");
        tariff.setCreatedAt(LocalDateTime.now());
        tariff.setUpdatedAt(LocalDateTime.now());

        tariff2 = new Tariff();
        tariff2.setId(1L);
        tariff2.setName("Test tariff.");
        tariff2.setRateHistory(new ArrayList<>());
        tariff2.setCreatedAt(LocalDateTime.now());
        tariff2.setUpdatedAt(LocalDateTime.now());

        RateHistory rateHistory1 = new RateHistory();
        rateHistory1.setRate(10.0);
        rateHistory1.setTariff(tariff);

        RateHistory rateHistory2 = new RateHistory();
        rateHistory2.setRate(20.0);
        rateHistory2.setTariff(tariff);

        tariff.setRateHistory(Arrays.asList(rateHistory1, rateHistory2));

        tariffDto = new TariffDto();
        tariffDto.setId(2L);
        tariffDto.setName("Test tariffDto.");

        rateHistory = Arrays.asList(10.0, 20.0);
    }

    @Test
    @DisplayName("Test create tariff.")
    public void testCreateTariff() {
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);
        when(tariffMapper.toDto(any(Tariff.class))).thenReturn(tariffDto);

        TariffDto result = tariffService.createTariff("Test tariff.", rateHistory);

        assertNotNull(result);
        assertEquals(tariffDto, result);
        verify(tariffRepository, times(1)).save(any(Tariff.class));
        verify(tariffMapper, times(1)).toDto(any(Tariff.class));
    }

    @Test
    @DisplayName("Test update tariff.")
    public void testUpdateTariff() {
        when(tariffRepository.findById(anyLong())).thenReturn(Optional.of(tariff2));
        when(tariffMapper.toEntity(any(TariffDto.class))).thenReturn(tariff2);
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff2);
        when(tariffMapper.toDto(any(Tariff.class))).thenReturn(tariffDto);

        TariffDto result = tariffService.updateTariff(1L, 15.0);

        assertNotNull(result);
        assertEquals(tariffDto, result);
        verify(tariffRepository, times(1)).findById(1L);
        verify(tariffMapper, times(1)).toEntity(any(TariffDto.class));
        verify(tariffRepository, times(1)).save(any(Tariff.class));
        verify(tariffMapper, times(1)).toDto(any(Tariff.class));

        assertEquals(1, tariff2.getRateHistory().size());
        assertEquals(15.0, tariff2.getRateHistory().get(0).getRate());
    }

    @Test
    @DisplayName("Test get tariff.")
    public void testGetTariff() {
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));
        when(tariffMapper.toDto(tariff)).thenReturn(tariffDto);

        TariffDto result = tariffService.getTariffById(1L);

        assertNotNull(result);
        assertEquals(tariffDto, result);
        verify(tariffRepository, times(1)).findById(1L);
        verify(tariffMapper, times(1)).toDto(tariff);
    }

    @Test
    @DisplayName("Test get tariff not exist.")
    public void testGetTariffWithException() {
        when(tariffRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> tariffService.getTariffById(1L));

        assertEquals("There is no tariff under this ID - 1", exception.getMessage());
        verify(tariffRepository, times(1)).findById(1L);
        verify(tariffMapper, times(0)).toDto(any());
    }
}
