package faang.school.accountservice.service.cashback;

import faang.school.accountservice.dto.cashback.CashbackMappingDto;
import faang.school.accountservice.dto.cashback.CashbackTariffDto;
import faang.school.accountservice.enums.MappingType;
import faang.school.accountservice.mapper.CashbackTariffMapper;
import faang.school.accountservice.model.cashback.CashbackId;
import faang.school.accountservice.model.cashback.CashbackTariff;
import faang.school.accountservice.model.cashback.Merchant;
import faang.school.accountservice.model.cashback.MerchantCashback;
import faang.school.accountservice.model.cashback.OperationCashback;
import faang.school.accountservice.model.cashback.OperationType;
import faang.school.accountservice.repository.cashback.CashbackTariffRepository;
import faang.school.accountservice.repository.cashback.MerchantCashbackRepository;
import faang.school.accountservice.repository.cashback.MerchantRepository;
import faang.school.accountservice.repository.cashback.OperationCashbackRepository;
import faang.school.accountservice.repository.cashback.OperationTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashbackTariffServiceImplTest {
    @Mock
    private CashbackTariffRepository cashbackTariffRepository;
    @Mock
    private CashbackTariffMapper cashbackTariffMapper;
    @Mock
    private MerchantCashbackRepository merchantCashbackRepository;
    @Mock
    private OperationCashbackRepository operationCashbackRepository;
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private OperationTypeRepository operationTypeRepository;
    @Mock
    private CashbackMappingFactory cashbackMappingFactory;

    @InjectMocks
    private CashbackTariffServiceImpl cashbackTariffService;

    private CashbackTariff cashbackTariff;
    private CashbackTariffDto cashbackTariffDto;
    private MerchantCashback merchantCashback;
    private OperationCashback operationCashback;
    private CashbackMappingDto merchantMappingDto;
    private CashbackMappingDto operationMappingDto;

    @BeforeEach
    void setUp() {
        cashbackTariff = new CashbackTariff();
        cashbackTariff.setId(1L);
        cashbackTariff.setName("Test Tariff");

        cashbackTariffDto = new CashbackTariffDto();
        cashbackTariffDto.setId(1L);
        cashbackTariffDto.setName("Test Tariff");

        merchantCashback = new MerchantCashback();
        merchantCashback.setTariffId(1L);
        merchantCashback.setTypeId(1L);
        merchantCashback.setPercentage(5.0);

        operationCashback = new OperationCashback();
        operationCashback.setTariffId(1L);
        operationCashback.setTypeId(1L);
        operationCashback.setPercentage(3.0);

        merchantMappingDto = new CashbackMappingDto();
        merchantMappingDto.setTariffId(1L);
        merchantMappingDto.setTypeId(1L);
        merchantMappingDto.setMappingType(MappingType.MERCHANT);
        merchantMappingDto.setCashbackPercentage(5.0);

        operationMappingDto = new CashbackMappingDto();
        operationMappingDto.setTariffId(1L);
        operationMappingDto.setTypeId(1L);
        operationMappingDto.setMappingType(MappingType.OPERATION);
        operationMappingDto.setCashbackPercentage(3.0);
    }

    @Test
    @DisplayName("Should successfully retrieve tariff by id")
    void shouldReturnTariffWhenExists() {
        when(cashbackTariffRepository.findById(1L)).thenReturn(Optional.of(cashbackTariff));
        when(cashbackTariffMapper.toDto(cashbackTariff)).thenReturn(cashbackTariffDto);
        CashbackTariffDto result = cashbackTariffService.getTariff(1L);
        verify(cashbackTariffRepository).findById(1L);
        verify(cashbackTariffMapper).toDto(cashbackTariff);
        assertNotNull(result);
        assertEquals(cashbackTariffDto.getId(), result.getId());
        assertEquals(cashbackTariffDto.getName(), result.getName());

    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when tariff not found")
    void shouldThrowExceptionWhenTariffNotFound() {
        when(cashbackTariffRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> cashbackTariffService.getTariff(1L));
        verify(cashbackTariffRepository).findById(1L);
    }

    @Test
    @DisplayName("Should successfully create new tariff")
    void shouldCreateNewTariff() {
        when(cashbackTariffMapper.toEntity(cashbackTariffDto)).thenReturn(cashbackTariff);
        when(cashbackTariffRepository.save(cashbackTariff)).thenReturn(cashbackTariff);
        when(cashbackTariffMapper.toDto(cashbackTariff)).thenReturn(cashbackTariffDto);
        CashbackTariffDto result = cashbackTariffService.createTariff(cashbackTariffDto);
        verify(cashbackTariffRepository).save(cashbackTariff);
        assertNotNull(result);
        assertEquals(cashbackTariffDto.getName(), result.getName());
    }

    @Test
    @DisplayName("Should successfully update existing tariff")
    void shouldUpdateExistingTariff() {
        when(cashbackTariffRepository.findById(1L)).thenReturn(Optional.of(cashbackTariff));
        when(cashbackTariffRepository.save(any(CashbackTariff.class))).thenReturn(cashbackTariff);
        when(cashbackTariffMapper.toDto(cashbackTariff)).thenReturn(cashbackTariffDto);
        CashbackTariffDto result = cashbackTariffService.updateTariff(cashbackTariffDto, 1L);
        verify(cashbackTariffRepository).save(cashbackTariff);
        assertNotNull(result);
        assertEquals(cashbackTariffDto.getName(), result.getName());

    }

    @Test
    @DisplayName("Should successfully delete existing tariff")
    void shouldDeleteExistingTariff() {
        when(cashbackTariffRepository.findById(1L)).thenReturn(Optional.of(cashbackTariff));
        doNothing().when(cashbackTariffRepository).delete(cashbackTariff);
        assertDoesNotThrow(() -> cashbackTariffService.deleteTariff(1L));
        verify(cashbackTariffRepository).delete(cashbackTariff);
    }

    @Test
    @DisplayName("Should return merchant cashback mapping when exists")
    void shouldReturnMerchantCashbackMapping() {
        CashbackId cashbackId = new CashbackId(1L, 1L);
        when(merchantCashbackRepository.findById(cashbackId)).thenReturn(Optional.of(merchantCashback));
        CashbackMappingDto result = cashbackTariffService.getCashbackMapping(merchantMappingDto);
        verify(merchantCashbackRepository).findById(cashbackId);
        assertNotNull(result);
        assertEquals(merchantCashback.getPercentage(), result.getCashbackPercentage());
    }

    @Test
    @DisplayName("Should return operation cashback mapping when exists")
    void shouldReturnOperationCashbackMapping() {
        CashbackId cashbackId = new CashbackId(1L, 1L);
        when(operationCashbackRepository.findById(cashbackId)).thenReturn(Optional.of(operationCashback));
        CashbackMappingDto result = cashbackTariffService.getCashbackMapping(operationMappingDto);
        verify(operationCashbackRepository).findById(cashbackId);
        assertNotNull(result);
        assertEquals(operationCashback.getPercentage(), result.getCashbackPercentage());
    }

    @Test
    @DisplayName("Should successfully create merchant cashback mapping")
    void shouldCreateMerchantCashbackMapping() {
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(new Merchant()));
        when(cashbackTariffRepository.findById(1L)).thenReturn(Optional.of(cashbackTariff));
        doReturn(merchantCashback).when(cashbackMappingFactory).createMapping(merchantMappingDto);
        doReturn(merchantCashback).when(merchantCashbackRepository).save(any(MerchantCashback.class));
        CashbackMappingDto result = cashbackTariffService.createCashbackMapping(merchantMappingDto);
        verify(merchantCashbackRepository).save(any(MerchantCashback.class));
        assertNotNull(result);
        assertEquals(merchantMappingDto.getCashbackPercentage(), result.getCashbackPercentage());
    }

    @Test
    @DisplayName("Should successfully create operation cashback mapping")
    void shouldCreateOperationCashbackMapping() {
        when(operationTypeRepository.findById(1L)).thenReturn(Optional.of(new OperationType()));
        when(cashbackTariffRepository.findById(1L)).thenReturn(Optional.of(cashbackTariff));
        doReturn(operationCashback).when(cashbackMappingFactory).createMapping(operationMappingDto);
        when(operationCashbackRepository.save(operationCashback)).thenReturn(operationCashback);
        CashbackMappingDto result = cashbackTariffService.createCashbackMapping(operationMappingDto);
        verify(operationCashbackRepository).save(operationCashback);
        assertNotNull(result);
        assertEquals(operationMappingDto.getCashbackPercentage(), result.getCashbackPercentage());
    }

    @Test
    @DisplayName("Should successfully delete merchant cashback mapping")
    void shouldDeleteMerchantCashbackMapping() {
        CashbackId cashbackId = new CashbackId(1L, 1L);
        when(merchantCashbackRepository.findById(cashbackId)).thenReturn(Optional.of(merchantCashback));
        doNothing().when(merchantCashbackRepository).deleteById(cashbackId);
        assertDoesNotThrow(() -> cashbackTariffService.deleteCashbackMapping(merchantMappingDto));
        verify(merchantCashbackRepository).deleteById(cashbackId);
    }

    @Test
    @DisplayName("Should successfully delete operation cashback mapping")
    void shouldDeleteOperationCashbackMapping() {
        CashbackId cashbackId = new CashbackId(1L, 1L);
        when(operationCashbackRepository.findById(cashbackId)).thenReturn(Optional.of(operationCashback));
        doNothing().when(operationCashbackRepository).deleteById(cashbackId);
        assertDoesNotThrow(() -> cashbackTariffService.deleteCashbackMapping(operationMappingDto));
        verify(operationCashbackRepository).deleteById(cashbackId);
    }
}