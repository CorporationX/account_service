package faang.school.accountservice.service;

import faang.school.accountservice.dto.OperationTypeMappingDto;
import faang.school.accountservice.dto.OperationTypeMappingUpdateDto;
import faang.school.accountservice.entity.OperationTypeMapping;
import faang.school.accountservice.mapper.OperationTypeMappingMapper;
import faang.school.accountservice.repository.OperationTypeMappingRepository;
import faang.school.accountservice.util.PercentageCalculator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationTypeMappingServiceImpl implements OperationTypeMappingService {

    private final OperationTypeMappingRepository operationTypeMappingRepository;
    private final OperationTypeMappingMapper operationTypeMappingMapper;
    private final PercentageCalculator percentageCalculator;

    @Override
    public OperationTypeMappingDto createOperationTypeMapping(OperationTypeMappingDto dto) {
        OperationTypeMapping operationTypeMapping = operationTypeMappingMapper.toEntity(dto);
        operationTypeMapping = operationTypeMappingRepository.save(operationTypeMapping);
        return operationTypeMappingMapper.toDto(operationTypeMapping);
    }

    @Override
    public OperationTypeMappingDto getOperationTypeMappingById(long id) {
        return operationTypeMappingRepository.findById(id)
                .map(operationTypeMappingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Operation type mapping with id %d not found".formatted(id)));
    }

    @Override
    @Transactional
    public OperationTypeMappingDto updateOperationTypeMapping(OperationTypeMappingUpdateDto operationTypeMappingUpdateDto) {
        long id = operationTypeMappingUpdateDto.getId();
        OperationTypeMapping operationTypeMapping = operationTypeMappingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Operation type mapping with id %d not found".formatted(id)));
        operationTypeMappingMapper.updateOperationTypeMapping(operationTypeMappingUpdateDto, operationTypeMapping);
        return operationTypeMappingMapper.toDto(operationTypeMapping);
    }

    @Override
    public void deleteOperationTypeMappingById(long id) {
        operationTypeMappingRepository.deleteById(id);
    }

    @Override
    public BigDecimal applyCashbackToAmount(@NotNull List<OperationTypeMapping> operationTypeMappings,
                                            @NotNull BigDecimal amount) {
        BigDecimal maxAmountWithCashback = BigDecimal.ZERO;
        for (OperationTypeMapping operationTypeMappingDto : operationTypeMappings) {
            BigDecimal percentage = operationTypeMappingDto.getPercentage();
            BigDecimal amountWithCashback = percentageCalculator.calculatePercentageNumber(amount, percentage);
            maxAmountWithCashback = maxAmountWithCashback.max(amountWithCashback);
        }
        return maxAmountWithCashback;
    }
}

