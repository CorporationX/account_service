package faang.school.accountservice.service;

import faang.school.accountservice.dto.MerchantMappingDto;
import faang.school.accountservice.dto.MerchantMappingUpdateDto;
import faang.school.accountservice.entity.MerchantMapping;
import faang.school.accountservice.mapper.MerchantMappingMapperImpl;
import faang.school.accountservice.repository.MerchantMappingRepository;
import faang.school.accountservice.util.PercentageCalculator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MerchantMappingServiceImplTest {

    @InjectMocks
    private MerchantMappingServiceImpl merchantMappingService;

    @Mock
    private MerchantMappingRepository merchantMappingRepository;

    @Spy
    private MerchantMappingMapperImpl merchantMappingMapper;

    @Mock
    private PercentageCalculator percentageCalculator;

    private MerchantMappingDto merchantMappingDto;
    private MerchantMapping merchantMapping;
    private MerchantMappingUpdateDto merchantMappingUpdateDto;
    private long merchantMappingId;
    private long merchantId;
    private BigDecimal amount;

    @BeforeEach
    public void setUp() {
        merchantId = 1L;
        merchantMappingId = 1L;
        merchantMappingDto = MerchantMappingDto.builder()
                .id(merchantMappingId)
                .id(2L)
                .percentage(BigDecimal.valueOf(10))
                .build();
        merchantMapping = MerchantMapping.builder()
                .id(merchantMappingId)
                .id(2L)
                .percentage(BigDecimal.valueOf(10))
                .build();
        merchantMappingUpdateDto = MerchantMappingUpdateDto.builder()
                .id(merchantMappingId)
                .percentage(BigDecimal.valueOf(15))
                .build();
        amount = new BigDecimal("100.00");
    }

    @Test
    public void testCreateMerchantMapping() {
        when(merchantMappingRepository.save(any(MerchantMapping.class))).thenReturn(merchantMapping);

        MerchantMappingDto result = merchantMappingService.createMerchantMapping(merchantMappingDto);

        assertEquals(merchantMappingDto, result);
        verify(merchantMappingMapper).toEntity(merchantMappingDto);
        verify(merchantMappingRepository).save(merchantMapping);
        verify(merchantMappingMapper).toDto(merchantMapping);
    }

    @Test
    void getMerchantMappingById_Success() {
        when(merchantMappingRepository.findById(merchantMappingId)).thenReturn(Optional.of(merchantMapping));

        MerchantMappingDto result = merchantMappingService.getMerchantMappingById(merchantMappingId);

        assertNotNull(result);
        assertEquals(merchantMappingDto, result);
        verify(merchantMappingRepository).findById(merchantMappingId);
        verify(merchantMappingMapper).toDto(merchantMapping);
    }

    @Test
    void getMerchantMappingById_EntityNotFound() {
        String correctMessage = "Merchant mapping with id %d not found".formatted(merchantMappingId);
        when(merchantMappingRepository.findById(merchantMappingId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> merchantMappingService.getMerchantMappingById(merchantMappingId));

        assertEquals(correctMessage, thrown.getMessage());
        verify(merchantMappingRepository).findById(merchantMappingId);
        verify(merchantMappingMapper, never()).toDto(any());
    }

    @Test
    void updateMerchantMapping_Success() {
        when(merchantMappingRepository.findById(merchantMappingId)).thenReturn(Optional.of(merchantMapping));
        doNothing().when(merchantMappingMapper).updateMerchantMapping(merchantMappingUpdateDto, merchantMapping);

        MerchantMappingDto result = merchantMappingService.updateMerchantMapping(merchantMappingUpdateDto);

        assertNotNull(result);
        assertEquals(merchantMappingDto, result);
        verify(merchantMappingRepository).findById(merchantMappingId);
        verify(merchantMappingMapper).updateMerchantMapping(merchantMappingUpdateDto, merchantMapping);
        verify(merchantMappingMapper).toDto(merchantMapping);
    }

    @Test
    void updateMerchantMapping_EntityNotFound() {
        String correctMessage = "Merchant mapping with id %d not found".formatted(merchantMappingId);
        when(merchantMappingRepository.findById(merchantMappingId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> merchantMappingService.updateMerchantMapping(merchantMappingUpdateDto));

        assertEquals(correctMessage, thrown.getMessage());
        verify(merchantMappingRepository).findById(merchantMappingId);
        verify(merchantMappingMapper, never()).updateMerchantMapping(any(), any());
        verify(merchantMappingMapper, never()).toDto(any());
    }

    @Test
    void deleteMerchantMappingById_Success() {
        merchantMappingService.deleteMerchantMappingById(merchantMappingId);

        verify(merchantMappingRepository).deleteById(merchantMappingId);
    }

    @Test
    void applyCashbackToAmount_ReturnsMaxCashbackForGivenMerchantId() {
        MerchantMapping mapping1 = MerchantMapping.builder()
                .merchantId(merchantId)
                .percentage(new BigDecimal("0.10"))
                .build();
        MerchantMapping mapping2 = MerchantMapping.builder()
                .percentage(new BigDecimal("0.20"))
                .build();
        MerchantMapping mapping3 = MerchantMapping.builder()
                .merchantId(merchantId)
                .percentage(new BigDecimal("0.15"))
                .build();
        List<MerchantMapping> merchantMappings = Arrays.asList(mapping1, mapping2, mapping3);

        when(percentageCalculator.calculatePercentageNumber(amount, mapping1.getPercentage())).thenReturn(new BigDecimal("10.00"));
        when(percentageCalculator.calculatePercentageNumber(amount, mapping3.getPercentage())).thenReturn(new BigDecimal("15.00"));

        BigDecimal result = merchantMappingService.applyCashbackToAmount(merchantMappingId, merchantMappings, amount);

        assertNotNull(result);
        assertEquals(new BigDecimal("15.00"), result);
        verify(percentageCalculator).calculatePercentageNumber(amount, mapping1.getPercentage());
        verify(percentageCalculator).calculatePercentageNumber(amount, mapping3.getPercentage());
        verify(percentageCalculator, never()).calculatePercentageNumber(amount, mapping2.getPercentage());
    }

    @Test
    void applyCashbackToAmount_ReturnsZero_WhenNoMatchingMerchantId() {
        List<MerchantMapping> merchantMappings = Collections.singletonList(merchantMapping);
        BigDecimal result = merchantMappingService.applyCashbackToAmount(merchantMappingId, merchantMappings, amount);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void applyCashbackToAmount_ReturnsZero_WhenMerchantMappingsAreEmpty() {
        List<MerchantMapping> merchantMappings = Collections.emptyList();

        BigDecimal result = merchantMappingService.applyCashbackToAmount(merchantMappingId, merchantMappings, amount);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
    }
}
