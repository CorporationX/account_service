package faang.school.accountservice.service;

import faang.school.accountservice.model.savings.Tariff;
import faang.school.accountservice.model.savings.TariffType;
import faang.school.accountservice.repository.TariffHistoryRepository;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TariffServiceTest {

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffHistoryRepository tariffHistoryRepository;

    @InjectMocks
    private TariffService tariffService;

    private UUID tariffId;
    private Tariff tariff;
    private String tariffType;
    private Double rateValue;

    @BeforeEach
    void setUp() {
        tariffId = UUID.randomUUID();
        tariffType = "BASIC";
        rateValue = 5.0;

        tariff = Tariff.builder()
                .id(tariffId)
                .type(TariffType.BASIC)
                .rateHistory(new ArrayList<>())
                .build();
    }

    @Test
    void testUpdateTariffRate() {
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));
        when(tariffRepository.save(any(Tariff.class))).thenReturn(tariff);

        Tariff updatedTariff = tariffService.updateTariffRate(tariffId, rateValue);

        assertNotNull(updatedTariff);
        assertEquals(tariffId, updatedTariff.getId());
        assertTrue(updatedTariff.getRateHistory().stream().anyMatch(rate -> rate.getRate() == rateValue));
        verify(tariffRepository).findById(tariffId);
        verify(tariffRepository).save(updatedTariff);
    }

    @Test
    void testUpdateTariffRateTariffNotFound() {
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tariffService.updateTariffRate(tariffId, rateValue));
    }

    @Test
    void testGetTariffByIdFound() {
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));

        Tariff foundTariff = tariffService.getTariffById(tariffId);

        assertNotNull(foundTariff);
        assertEquals(tariffId, foundTariff.getId());
        verify(tariffRepository).findById(tariffId);
    }

    @Test
    void testGetTariffByIdNotFound() {
        when(tariffRepository.findById(tariffId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tariffService.getTariffById(tariffId));
    }
}

