package faang.school.accountservice.controller;

import faang.school.accountservice.dto.MappingTypeDto;
import faang.school.accountservice.service.type.OperationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/operationType")
public class OperationTypeController {
    private final OperationTypeService operationTypeService;

    @GetMapping("/{id}")
    public MappingTypeDto getOperationType(@PathVariable Long id) {
        return operationTypeService.get(id);
    }

    @PostMapping
    public MappingTypeDto createOperationType(@RequestBody MappingTypeDto mappingTypeDto) {
        return operationTypeService.create(mappingTypeDto);
    }

    @PutMapping
    public MappingTypeDto updateOperationType(@RequestBody MappingTypeDto mappingTypeDto) {
        return operationTypeService.update(mappingTypeDto);
    }

    @DeleteMapping("/{id}")
    public void deleteOperationType(@PathVariable Long id) {
        operationTypeService.delete(id);
    }
}
