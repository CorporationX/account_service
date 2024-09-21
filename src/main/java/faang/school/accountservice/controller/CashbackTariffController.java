package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CashbackMerchantMappingDto;
import faang.school.accountservice.dto.CashbackOperationMappingDto;
import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.service.CashbackTariffService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cashback-tariffs")
public class CashbackTariffController {
    private final CashbackTariffService cashbackTariffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CashbackTariffDto createTariff(@Valid @RequestBody CashbackTariffDto cashbackTariffDto) {
        return cashbackTariffService.createTariff(cashbackTariffDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CashbackTariffDto getTariff(@Positive @PathVariable Long id) {
        return cashbackTariffService.getTariff(id);
    }

    @PostMapping("/{id}/operation-mappings")
    @ResponseStatus(HttpStatus.CREATED)
    public CashbackOperationMappingDto addOperationMapping(
        @Positive @PathVariable Long id,
        @Valid @RequestBody CashbackOperationMappingDto cashbackOperationMappingDto
    ) {
        return cashbackTariffService.addOperationMapping(id, cashbackOperationMappingDto);
    }

    @PostMapping("/{id}/merchant-mappings")
    @ResponseStatus(HttpStatus.CREATED)
    public CashbackMerchantMappingDto addMerchantMapping(
        @Positive @PathVariable Long id,
        @Valid @RequestBody CashbackMerchantMappingDto cashbackMerchantMappingDto
    ) {
        return cashbackTariffService.addMerchantMapping(id, cashbackMerchantMappingDto);
    }

    @GetMapping("/{id}/operation-mappings")
    @ResponseStatus(HttpStatus.OK)
    public List<CashbackOperationMappingDto> getOperationMappings(@Positive @PathVariable Long id) {
        return cashbackTariffService.getOperationMappings(id);
    }

    @GetMapping("/{id}/merchant-mappings")
    @ResponseStatus(HttpStatus.OK)
    public List<CashbackMerchantMappingDto> getMerchantMappings(@Positive @PathVariable Long id) {
        return cashbackTariffService.getMerchantMappings(id);
    }

    @PutMapping("/operation-mappings/{mappingId}")
    @ResponseStatus(HttpStatus.OK)
    public CashbackOperationMappingDto updateOperationMapping(
        @Positive @PathVariable Long mappingId,
        @Valid @RequestBody CashbackOperationMappingDto cashbackOperationMappingDto
    ) {
        return cashbackTariffService.updateOperationMapping(mappingId, cashbackOperationMappingDto);
    }

    @PutMapping("/merchant-mappings/{mappingId}")
    @ResponseStatus(HttpStatus.OK)
    public CashbackMerchantMappingDto updateMerchantMapping(
        @Positive @PathVariable Long mappingId,
        @Valid @RequestBody CashbackMerchantMappingDto cashbackMerchantMappingDto
    ) {
        return cashbackTariffService.updateMerchantMapping(mappingId, cashbackMerchantMappingDto);
    }

    @DeleteMapping("/operation-mappings/{mappingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOperationMapping(@Positive @PathVariable Long mappingId) {
        cashbackTariffService.deleteOperationMapping(mappingId);
    }

    @DeleteMapping("/merchant-mappings/{mappingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMerchantMapping(@Positive @PathVariable Long mappingId) {
        cashbackTariffService.deleteMerchantMapping(mappingId);
    }
}

