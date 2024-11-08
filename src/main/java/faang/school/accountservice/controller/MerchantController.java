package faang.school.accountservice.controller;

import faang.school.accountservice.dto.MappingTypeDto;
import faang.school.accountservice.service.type.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/merchant")
public class MerchantController {
    private final MerchantService merchantService;

    @GetMapping("/{id}")
    public MappingTypeDto getMerchant(@PathVariable Long id) {
        return merchantService.get(id);
    }

    @PostMapping
    public MappingTypeDto createMerchant(@RequestBody MappingTypeDto mappingTypeDto) {
        return merchantService.create(mappingTypeDto);
    }

    @PutMapping
    public MappingTypeDto updateMerchant(@RequestBody MappingTypeDto mappingTypeDto) {
        return merchantService.update(mappingTypeDto);
    }

    @DeleteMapping("/{id}")
    public void deleteMerchant(@PathVariable Long id) {
        merchantService.delete(id);
    }
}
