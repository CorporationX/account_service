package faang.school.accountservice.controller;

import faang.school.accountservice.dto.MerchantMappingDto;
import faang.school.accountservice.dto.MerchantMappingUpdateDto;
import faang.school.accountservice.service.MerchantMappingService;
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
@RequestMapping("/api/v1/merchant-mapping")
@Validated
@RequiredArgsConstructor
public class MerchantMappingController {

    private final MerchantMappingService merchantMappingService;

    @PostMapping
    public MerchantMappingDto createMerchantMapping(@RequestBody MerchantMappingDto dto) {
        return merchantMappingService.createMerchantMapping(dto);
    }

    @GetMapping("/{id}")
    public MerchantMappingDto getMerchantMappingById(@PathVariable @Positive Long id) {
        return merchantMappingService.getMerchantMappingById(id);
    }

    @PutMapping
    public MerchantMappingDto updateMerchantMapping(@RequestBody MerchantMappingUpdateDto dto) {
        return merchantMappingService.updateMerchantMapping(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteMerchantMappingById(@PathVariable @Positive Long id) {
        merchantMappingService.deleteMerchantMappingById(id);
    }
}

