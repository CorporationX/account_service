package faang.school.accountservice.controller;

import faang.school.accountservice.dto.OperationTypeMappingDto;
import faang.school.accountservice.dto.OperationTypeMappingUpdateDto;
import faang.school.accountservice.service.OperationTypeMappingService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/operation-type-mapping")
@Validated
@RequiredArgsConstructor
public class OperationTypeMappingController {

    private final OperationTypeMappingService operationTypeMappingService;

    @PostMapping
    public OperationTypeMappingDto createOperationTypeMapping(@RequestBody OperationTypeMappingDto dto) {
        return operationTypeMappingService.createOperationTypeMapping(dto);
    }

    @GetMapping("/{id}")
    public OperationTypeMappingDto getOperationTypeMappingById(@PathVariable @Positive Long id) {
        return operationTypeMappingService.getOperationTypeMappingById(id);
    }

    @PutMapping
    public OperationTypeMappingDto updateOperationTypeMapping(@RequestBody OperationTypeMappingUpdateDto dto) {
        return operationTypeMappingService.updateOperationTypeMapping(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteOperationTypeMappingById(@PathVariable @Positive Long id) {
        operationTypeMappingService.deleteOperationTypeMappingById(id);
    }
}
