package faang.school.accountservice.service.type;

import faang.school.accountservice.dto.MappingTypeDto;
import faang.school.accountservice.entity.type.OperationType;
import faang.school.accountservice.mapper.MappingTypeMapper;
import faang.school.accountservice.repository.OperationTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationTypeServiceImpl implements OperationTypeService {
    private final OperationTypeRepository operationTypeRepository;
    private final MappingTypeMapper mappingTypeMapper;

    @Override
    public MappingTypeDto get(Long id) {
        return operationTypeRepository.findById(id)
                .map(mappingTypeMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id: " + id));
    }

    @Override
    public MappingTypeDto create(MappingTypeDto mappingTypeDto) {
        OperationType operationType = new OperationType();
        operationType.setName(mappingTypeDto.getName());
        operationType = operationTypeRepository.save(operationType);

        return mappingTypeMapper.toDto(operationType);
    }
    @Override
    public MappingTypeDto update(MappingTypeDto mappingTypeDto) {
        OperationType operationType = operationTypeRepository.findById(mappingTypeDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id: " + mappingTypeDto.getId()));

        operationType.setName(mappingTypeDto.getName());
        operationTypeRepository.save(operationType);

        return mappingTypeMapper.toDto(operationType);
    }
    @Override
    public void delete(Long id) {
        OperationType operationType = operationTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id: " + id));
        operationTypeRepository.delete(operationType);
    }
}
