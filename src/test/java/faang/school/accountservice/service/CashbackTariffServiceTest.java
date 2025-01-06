package faang.school.accountservice.service;

import faang.school.accountservice.dto.cashbackdto.CashbackMappingDto;
import faang.school.accountservice.dto.cashbackdto.CashbackMappingType;
import faang.school.accountservice.dto.cashbackdto.CashbackTariffDto;
import faang.school.accountservice.model.CashbackOperationMapping;
import faang.school.accountservice.model.CashbackTariff;
import faang.school.accountservice.repository.CashbackMerchantMappingRepository;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CashbackTariffServiceTest {

    @Mock
    private CashbackTariffRepository tariffRepository;

    @Mock
    private CashbackOperationMappingRepository operationMappingRepository;

    @Mock
    private CashbackMerchantMappingRepository merchantMappingRepository;

    @InjectMocks
    private CashbackTariffService cashbackTariffService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTariff() {
        CashbackTariff tariff = new CashbackTariff();
        tariff.setId(1L);
        tariff.setCreatedAt(LocalDateTime.now());
        when(tariffRepository.save(any(CashbackTariff.class))).thenReturn(tariff);

        Long id = cashbackTariffService.createTariff();

        assertEquals(1L, id);
        verify(tariffRepository, times(1)).save(any(CashbackTariff.class));
    }

    @Test
    void testGetTariffById() {
        CashbackTariff tariff = new CashbackTariff();
        tariff.setId(1L);
        tariff.setCreatedAt(LocalDateTime.now());
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));

        CashbackTariffDto dto = cashbackTariffService.getTariffById(1L);

        assertEquals(1L, dto.id());
        verify(tariffRepository, times(1)).findById(1L);
    }

    @Test
    void testAddMapping() {
        CashbackMappingDto dto = new CashbackMappingDto(CashbackMappingType.OPERATION, "key", BigDecimal.valueOf(10.0));

        cashbackTariffService.addMapping(1L, dto);

        verify(operationMappingRepository, times(1)).save(any(CashbackOperationMapping.class));
    }

    @Test
    void testUpdateMapping() {
        CashbackOperationMapping mapping = new CashbackOperationMapping();
        mapping.setCashbackPercent(BigDecimal.valueOf(10.0));
        when(operationMappingRepository.findByTariffIdAndOperationType(1L, "key")).thenReturn(Optional.of(mapping));

        CashbackMappingDto dto = new CashbackMappingDto(CashbackMappingType.OPERATION, "key", BigDecimal.valueOf(15.0));

        cashbackTariffService.updateMapping(1L, dto);

        assertEquals(BigDecimal.valueOf(15.0), mapping.getCashbackPercent());
        verify(operationMappingRepository, times(1)).save(mapping);
    }

    @Test
    void testDeleteMapping() {
        cashbackTariffService.deleteMapping(1L, "key");

        verify(operationMappingRepository, times(1)).deleteByTariffIdAndOperationType(1L, "key");
        verify(merchantMappingRepository, times(1)).deleteByTariffIdAndMerchant(1L, "key");
    }
}