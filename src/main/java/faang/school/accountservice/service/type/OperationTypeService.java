package faang.school.accountservice.service.type;

import faang.school.accountservice.dto.MappingTypeDto;

public interface OperationTypeService {
    MappingTypeDto get(Long id);
    MappingTypeDto create(MappingTypeDto cashbackMappingDto);
    MappingTypeDto update(MappingTypeDto cashbackMappingDto);
    void delete(Long id);
}
