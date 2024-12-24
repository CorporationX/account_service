package faang.school.accountservice.controller;

import faang.school.accountservice.dto.cashbackdto.CashbackMappingDto;
import faang.school.accountservice.dto.cashbackdto.CashbackTariffDto;
import faang.school.accountservice.service.CashbackTariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tariffs")
@RequiredArgsConstructor
public class CashbackTariffController {

    private final CashbackTariffService cashbackTariffService;

    @PostMapping("/create")
    public ResponseEntity<Long> createTariff() {
        return ResponseEntity.ok(cashbackTariffService.createTariff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashbackTariffDto> getTariffById(@PathVariable Long id) {
        return ResponseEntity.ok(cashbackTariffService.getTariffById(id));
    }

//    @PostMapping("/{id}/mappings")
//    public ResponseEntity<Void> addMapping(@PathVariable Long id, @RequestBody CashbackMappingDto mappingDto) {
//        cashbackTariffService.addMapping(id, mappingDto);
//        return ResponseEntity.ok().build();
//    }
//
//    @PutMapping("/{id}/mappings")
//    public ResponseEntity<Void> updateMapping(@PathVariable Long id, @RequestBody CashbackMappingDto mappingDto) {
//        cashbackTariffService.updateMapping(id, mappingDto);
//        return ResponseEntity.ok().build();
//    }
//
//    @DeleteMapping("/{id}/mappings")
//    public ResponseEntity<Void> deleteMapping(@PathVariable Long id, @RequestParam String mappingKey) {
//        cashbackTariffService.deleteMapping(id, mappingKey);
//        return ResponseEntity.ok().build();
//    }
}