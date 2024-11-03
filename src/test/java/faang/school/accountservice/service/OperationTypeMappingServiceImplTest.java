package faang.school.accountservice.service;

import faang.school.accountservice.dto.OperationTypeMappingDto;
import faang.school.accountservice.dto.OperationTypeMappingUpdateDto;
import faang.school.accountservice.entity.OperationTypeMapping;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.mapper.OperationTypeMappingMapperImpl;
import faang.school.accountservice.repository.OperationTypeMappingRepository;
import faang.school.accountservice.util.PercentageCalculator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationTypeMappingServiceImplTest {

    @Mock
    private OperationTypeMappingRepository operationTypeMappingRepository;

    @Spy
    private OperationTypeMappingMapperImpl operationTypeMappingMapper;

    @Mock
    private PercentageCalculator percentageCalculator;

    @InjectMocks
    private OperationTypeMappingServiceImpl operationTypeMappingService;

    private long operationTypeMappingId;
    private OperationTypeMapping operationTypeMapping;
    private OperationTypeMappingDto operationTypeMappingDto;
    private OperationTypeMappingUpdateDto operationTypeMappingUpdateDto;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        operationTypeMappingId = 1L;
        operationTypeMapping = OperationTypeMapping.builder()
                .id(operationTypeMappingId)
                .build();
        operationTypeMappingDto = OperationTypeMappingDto.builder()
                .id(operationTypeMappingId)
                .build();
        operationTypeMappingUpdateDto = OperationTypeMappingUpdateDto.builder()
                .id(operationTypeMappingId)
                .operationType(OperationType.REFUND)
                .build();
        amount = BigDecimal.valueOf(100);
    }

    @Test
    void createOperationTypeMapping_ShouldReturnDto_WhenCalled() {
        when(operationTypeMappingRepository.save(operationTypeMapping)).thenReturn(operationTypeMapping);

        OperationTypeMappingDto result = operationTypeMappingService.createOperationTypeMapping(operationTypeMappingDto);

        assertNotNull(result);
        assertEquals(operationTypeMappingDto, result);
        verify(operationTypeMappingMapper).toEntity(operationTypeMappingDto);
        verify(operationTypeMappingRepository).save(operationTypeMapping);
        verify(operationTypeMappingMapper).toDto(operationTypeMapping);
    }

    @Test
    void getOperationTypeMappingById_ShouldReturnDto_WhenEntityExists() {
        when(operationTypeMappingRepository.findById(operationTypeMappingId)).thenReturn(Optional.of(operationTypeMapping));

        OperationTypeMappingDto result = operationTypeMappingService.getOperationTypeMappingById(operationTypeMappingId);

        assertNotNull(result);
        assertEquals(operationTypeMappingDto, result);
        verify(operationTypeMappingRepository).findById(operationTypeMappingId);
        verify(operationTypeMappingMapper).toDto(operationTypeMapping);
    }

    @Test
    void getOperationTypeMappingById_ShouldThrowException_WhenEntityDoesNotExist() {
        String correctMessage = "Operation type mapping with id %d not found".formatted(operationTypeMappingId);
        when(operationTypeMappingRepository.findById(operationTypeMappingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> operationTypeMappingService.getOperationTypeMappingById(operationTypeMappingId));

        assertEquals(correctMessage, exception.getMessage());
        verify(operationTypeMappingRepository).findById(operationTypeMappingId);
        verify(operationTypeMappingMapper, never()).toDto(any(OperationTypeMapping.class));
    }

    @Test
    void updateOperationTypeMapping_ShouldReturnUpdatedDto_WhenEntityExists() {
        OperationType correctResult = OperationType.REFUND;
        when(operationTypeMappingRepository.findById(operationTypeMappingId)).thenReturn(Optional.of(operationTypeMapping));

        OperationTypeMappingDto result = operationTypeMappingService.updateOperationTypeMapping(operationTypeMappingUpdateDto);

        assertNotNull(result);
        assertEquals(correctResult, result.getOperationType());
        verify(operationTypeMappingRepository).findById(operationTypeMappingId);
        verify(operationTypeMappingMapper).updateOperationTypeMapping(operationTypeMappingUpdateDto, operationTypeMapping);
        verify(operationTypeMappingMapper).toDto(operationTypeMapping);
    }

    @Test
    void updateOperationTypeMapping_ShouldThrowException_WhenEntityDoesNotExist() {
        String correctMessage = "Operation type mapping with id %d not found".formatted(operationTypeMappingId);
        when(operationTypeMappingRepository.findById(operationTypeMappingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> operationTypeMappingService.updateOperationTypeMapping(operationTypeMappingUpdateDto));

        assertEquals(correctMessage, exception.getMessage());
        verify(operationTypeMappingRepository).findById(operationTypeMappingId);
        verify(operationTypeMappingMapper, never()).updateOperationTypeMapping(any(), any());
        verify(operationTypeMappingMapper, never()).toDto(any());
    }

    @Test
    void deleteOperationTypeMappingById_ShouldInvokeRepositoryMethod() {
        operationTypeMappingService.deleteOperationTypeMappingById(operationTypeMappingId);

        verify(operationTypeMappingRepository).deleteById(operationTypeMappingId);
    }

    @Test
    void applyCashbackToAmount_ShouldReturnMaxCashback_WhenMultipleMappings() {
        OperationTypeMapping mapping1 = OperationTypeMapping.builder()
                .percentage(new BigDecimal(10))
                .build();
        OperationTypeMapping mapping2 = OperationTypeMapping.builder()
                .percentage(new BigDecimal(20))
                .build();
        List<OperationTypeMapping> operationTypeMappings = List.of(mapping1, mapping2);

        when(percentageCalculator.calculatePercentageNumber(amount, mapping1.getPercentage())).thenReturn(mapping1.getPercentage());
        when(percentageCalculator.calculatePercentageNumber(amount, mapping2.getPercentage())).thenReturn(mapping2.getPercentage());

        BigDecimal result = operationTypeMappingService.applyCashbackToAmount(operationTypeMappings, amount);

        assertNotNull(result);
        assertEquals(new BigDecimal(20), result);
    }

    @Test
    void applyCashbackToAmount_ShouldReturnZero_WhenNoMappings() {
        BigDecimal correctResult = BigDecimal.ZERO;
        List<OperationTypeMapping> operationTypeMappings = Collections.emptyList();

        BigDecimal result = operationTypeMappingService.applyCashbackToAmount(operationTypeMappings, amount);

        assertNotNull(result);
        assertEquals(correctResult, result);
    }
}
