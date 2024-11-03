package faang.school.accountservice.service;

import faang.school.accountservice.dto.OperationTypeMappingDto;
import faang.school.accountservice.dto.OperationTypeMappingUpdateDto;
import faang.school.accountservice.entity.OperationTypeMapping;

import java.math.BigDecimal;
import java.util.List;

public interface OperationTypeMappingService {

    OperationTypeMappingDto createOperationTypeMapping(OperationTypeMappingDto dto);

    OperationTypeMappingDto getOperationTypeMappingById(long id);

    OperationTypeMappingDto updateOperationTypeMapping(OperationTypeMappingUpdateDto operationTypeMappingUpdateDto);

    void deleteOperationTypeMappingById(long id);

    BigDecimal applyCashbackToAmount(List<OperationTypeMapping> operationTypeMappings, BigDecimal amount);
}

