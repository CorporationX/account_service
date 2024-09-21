package faang.school.accountservice.service;

import faang.school.accountservice.dto.CashbackMerchantMappingDto;
import faang.school.accountservice.dto.CashbackOperationMappingDto;
import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.entity.CashbackMerchantMapping;
import faang.school.accountservice.entity.CashbackOperationMapping;
import faang.school.accountservice.entity.CashbackTariff;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.CashbackMerchantMappingMapper;
import faang.school.accountservice.mapper.CashbackOperationMappingMapper;
import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.repository.CashbackMerchantMappingRepository;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.validator.CashbackMerchantMappingValidator;
import faang.school.accountservice.validator.CashbackOperationMappingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashbackTariffServiceTest {

    @Mock
    CashbackTariffRepository cashbackTariffRepository;

    @Mock
    CashbackOperationMappingRepository cashbackOperationMappingRepository;

    @Mock
    CashbackMerchantMappingRepository cashbackMerchantMappingRepository;

    @Mock
    CashbackTariffMapper cashbackTariffMapper;

    @Mock
    CashbackOperationMappingMapper cashbackOperationMappingMapper;

    @Mock
    CashbackMerchantMappingMapper cashbackMerchantMappingMapper;

    @Mock
    CashbackOperationMappingValidator cashbackOperationMappingValidator;

    @Mock
    CashbackMerchantMappingValidator cashbackMerchantMappingValidator;

    @InjectMocks
    CashbackTariffService cashbackTariffService;

    CashbackTariff cashbackTariff;
    CashbackTariffDto cashbackTariffDto;
    CashbackOperationMapping cashbackOperationMapping;
    CashbackOperationMappingDto cashbackOperationMappingDto;
    CashbackMerchantMapping cashbackMerchantMapping;
    CashbackMerchantMappingDto cashbackMerchantMappingDto;
    Long cashbackTariffId = 1L;
    Long cashbackOperationMappingId = 2L;
    Long cashbackMerchantMappingId = 3L;

    @BeforeEach
    void setUp() {
        cashbackTariff = new CashbackTariff();
        cashbackTariffDto = new CashbackTariffDto();
        cashbackOperationMapping = new CashbackOperationMapping();
        cashbackOperationMappingDto = new CashbackOperationMappingDto();
        cashbackMerchantMapping = new CashbackMerchantMapping();
        cashbackMerchantMappingDto = new CashbackMerchantMappingDto();
    }

    @Test
    @DisplayName("When creating a tariff, then return the created tariff")
    void createTariff() {
        when(cashbackTariffMapper.toEntity(cashbackTariffDto)).thenReturn(cashbackTariff);
        when(cashbackTariffRepository.save(cashbackTariff)).thenReturn(cashbackTariff);
        when(cashbackTariffMapper.toDto(cashbackTariff)).thenReturn(cashbackTariffDto);

        CashbackTariffDto result = cashbackTariffService.createTariff(cashbackTariffDto);

        verify(cashbackTariffMapper).toEntity(cashbackTariffDto);
        verify(cashbackTariffRepository).save(cashbackTariff);
        verify(cashbackTariffMapper).toDto(cashbackTariff);
        assertNotNull(result);
        assertEquals(cashbackTariffDto, result);
    }

    @Test
    @DisplayName("When retrieving a tariff by ID, then return the corresponding tariff")
    void getTariff() {
        when(cashbackTariffRepository.findById(cashbackTariffId)).thenReturn(Optional.of(cashbackTariff));
        when(cashbackTariffMapper.toDto(cashbackTariff)).thenReturn(cashbackTariffDto);

        CashbackTariffDto result = cashbackTariffService.getTariff(cashbackTariffId);

        verify(cashbackTariffRepository).findById(cashbackTariffId);
        verify(cashbackTariffMapper).toDto(cashbackTariff);
        assertNotNull(result);
        assertEquals(cashbackTariffDto, result);
    }

    @Test
    @DisplayName("When retrieving a non-existing tariff by ID, then throw an EntityNotFoundException")
    void getTariff_NotFound() {
        when(cashbackTariffRepository.findById(cashbackTariffId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cashbackTariffService.getTariff(cashbackTariffId));
        verify(cashbackTariffRepository).findById(cashbackTariffId);
    }

    @Test
    @DisplayName("When adding an operation mapping, then return the added mapping")
    void addOperationMapping() {
        when(cashbackTariffRepository.findById(cashbackTariffId)).thenReturn(Optional.of(cashbackTariff));
        when(cashbackOperationMappingMapper.toEntity(cashbackOperationMappingDto)).thenReturn(cashbackOperationMapping);
        when(cashbackOperationMappingRepository.save(cashbackOperationMapping)).thenReturn(cashbackOperationMapping);
        when(cashbackOperationMappingMapper.toDto(cashbackOperationMapping)).thenReturn(cashbackOperationMappingDto);

        CashbackOperationMappingDto result = cashbackTariffService.addOperationMapping(cashbackTariffId, cashbackOperationMappingDto);

        verify(cashbackTariffRepository).findById(cashbackTariffId);
        verify(cashbackOperationMappingMapper).toEntity(cashbackOperationMappingDto);
        verify(cashbackOperationMappingRepository).save(cashbackOperationMapping);
        verify(cashbackOperationMappingMapper).toDto(cashbackOperationMapping);
        assertNotNull(result);
        assertEquals(cashbackOperationMappingDto, result);
    }

    @Test
    @DisplayName("When adding an operation mapping to a non-existing tariff, then throw an EntityNotFoundException")
    void addOperationMapping_TariffNotFound() {
        when(cashbackTariffRepository.findById(cashbackTariffId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cashbackTariffService.addOperationMapping(cashbackTariffId, cashbackOperationMappingDto));
        verify(cashbackTariffRepository).findById(cashbackTariffId);
    }


    @Test
    @DisplayName("When adding a merchant mapping, then return the added mapping")
    void addMerchantMapping() {
        when(cashbackTariffRepository.findById(cashbackTariffId)).thenReturn(Optional.of(cashbackTariff));
        when(cashbackMerchantMappingMapper.toEntity(cashbackMerchantMappingDto)).thenReturn(cashbackMerchantMapping);
        when(cashbackMerchantMappingRepository.save(cashbackMerchantMapping)).thenReturn(cashbackMerchantMapping);
        when(cashbackMerchantMappingMapper.toDto(cashbackMerchantMapping)).thenReturn(cashbackMerchantMappingDto);

        CashbackMerchantMappingDto result = cashbackTariffService.addMerchantMapping(cashbackTariffId, cashbackMerchantMappingDto);

        verify(cashbackTariffRepository).findById(cashbackTariffId);
        verify(cashbackMerchantMappingMapper).toEntity(cashbackMerchantMappingDto);
        verify(cashbackMerchantMappingRepository).save(cashbackMerchantMapping);
        verify(cashbackMerchantMappingMapper).toDto(cashbackMerchantMapping);
        assertNotNull(result);
        assertEquals(cashbackMerchantMappingDto, result);
    }

    @Test
    @DisplayName("When adding a merchant mapping to a non-existing tariff, then throw an EntityNotFoundException")
    void addMerchantMapping_TariffNotFound() {
        when(cashbackTariffRepository.findById(cashbackTariffId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cashbackTariffService.addMerchantMapping(cashbackTariffId, cashbackMerchantMappingDto));
        verify(cashbackTariffRepository).findById(cashbackTariffId);
    }

    @Test
    @DisplayName("When retrieving operation mappings, then return the list of mappings")
    void getOperationMappings() {
        when(cashbackOperationMappingRepository.findByCashbackTariffId(cashbackTariffId)).thenReturn(List.of(cashbackOperationMapping));
        when(cashbackOperationMappingMapper.toDto(cashbackOperationMapping)).thenReturn(cashbackOperationMappingDto);

        List<CashbackOperationMappingDto> result = cashbackTariffService.getOperationMappings(cashbackTariffId);

        verify(cashbackOperationMappingRepository).findByCashbackTariffId(cashbackTariffId);
        verify(cashbackOperationMappingMapper).toDto(cashbackOperationMapping);
        assertNotNull(result);
        assertEquals(List.of(cashbackOperationMappingDto), result);
    }

    @Test
    @DisplayName("When retrieving merchant mappings, then return the list of mappings")
    void getMerchantMappings() {
        when(cashbackMerchantMappingRepository.findByCashbackTariffId(cashbackTariffId)).thenReturn(List.of(cashbackMerchantMapping));
        when(cashbackMerchantMappingMapper.toDto(cashbackMerchantMapping)).thenReturn(cashbackMerchantMappingDto);

        List<CashbackMerchantMappingDto> result = cashbackTariffService.getMerchantMappings(cashbackTariffId);

        verify(cashbackMerchantMappingRepository).findByCashbackTariffId(cashbackTariffId);
        verify(cashbackMerchantMappingMapper).toDto(cashbackMerchantMapping);
        assertNotNull(result);
        assertEquals(List.of(cashbackMerchantMappingDto), result);
    }

    @Test
    @DisplayName("When updating an operation mapping, then return the updated mapping")
    void updateOperationMapping() {
        when(cashbackOperationMappingRepository.findById(cashbackOperationMappingId)).thenReturn(Optional.of(cashbackOperationMapping));
        when(cashbackOperationMappingMapper.toDto(cashbackOperationMapping)).thenReturn(cashbackOperationMappingDto);

        CashbackOperationMappingDto result = cashbackTariffService.updateOperationMapping(cashbackOperationMappingId, cashbackOperationMappingDto);

        verify(cashbackOperationMappingRepository).findById(cashbackOperationMappingId);
        verify(cashbackOperationMappingMapper).updateEntityFromDto(cashbackOperationMappingDto, cashbackOperationMapping);
        verify(cashbackOperationMappingMapper).toDto(cashbackOperationMapping);
        assertNotNull(result);
        assertEquals(cashbackOperationMappingDto, result);
    }

    @Test
    @DisplayName("When updating a non-existing operation mapping, then throw an EntityNotFoundException")
    void updateOperationMapping_NotFound() {
        when(cashbackOperationMappingRepository.findById(cashbackOperationMappingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cashbackTariffService.updateOperationMapping(cashbackOperationMappingId, cashbackOperationMappingDto));
        verify(cashbackOperationMappingRepository).findById(cashbackOperationMappingId);
    }

    @Test
    @DisplayName("When updating a merchant mapping, then return the updated mapping")
    void updateMerchantMapping() {
        when(cashbackMerchantMappingRepository.findById(cashbackMerchantMappingId)).thenReturn(Optional.of(cashbackMerchantMapping));
        when(cashbackMerchantMappingMapper.toDto(cashbackMerchantMapping)).thenReturn(cashbackMerchantMappingDto);

        CashbackMerchantMappingDto result = cashbackTariffService.updateMerchantMapping(cashbackMerchantMappingId, cashbackMerchantMappingDto);

        verify(cashbackMerchantMappingRepository).findById(cashbackMerchantMappingId);
        verify(cashbackMerchantMappingMapper).updateEntityFromDto(cashbackMerchantMappingDto, cashbackMerchantMapping);
        verify(cashbackMerchantMappingMapper).toDto(cashbackMerchantMapping);
        assertNotNull(result);
        assertEquals(cashbackMerchantMappingDto, result);
    }

    @Test
    @DisplayName("When updating a non-existing merchant mapping, then throw an EntityNotFoundException")
    void updateMerchantMapping_NotFound() {
        when(cashbackMerchantMappingRepository.findById(cashbackMerchantMappingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cashbackTariffService.updateMerchantMapping(cashbackMerchantMappingId, cashbackMerchantMappingDto));
        verify(cashbackMerchantMappingRepository).findById(cashbackMerchantMappingId);
    }

    @Test
    @DisplayName("When deleting an operation mapping, then it should be removed successfully")
    void deleteOperationMapping() {
        cashbackTariffService.deleteOperationMapping(cashbackOperationMappingId);

        verify(cashbackOperationMappingValidator).validateExists(cashbackOperationMappingId);
        verify(cashbackOperationMappingRepository).deleteById(cashbackOperationMappingId);
    }

    @Test
    @DisplayName("When deleting a merchant mapping, then it should be removed successfully")
    void deleteMerchantMapping() {
        cashbackTariffService.deleteMerchantMapping(cashbackMerchantMappingId);

        verify(cashbackMerchantMappingValidator).validateExists(cashbackMerchantMappingId);
        verify(cashbackMerchantMappingRepository).deleteById(cashbackMerchantMappingId);
    }
}