package faang.school.accountservice.controller;

import faang.school.accountservice.dto.cashbackdto.CashbackMappingDto;
import faang.school.accountservice.dto.cashbackdto.CashbackTariffDto;
import faang.school.accountservice.service.CashbackTariffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@Slf4j
public class CashbackTariffController {

    private final CashbackTariffService cashbackTariffService;

    @PostMapping("/create")
    public ResponseEntity<Long> createTariff() {
        log.info("Create Tariff");
        return ResponseEntity.status(HttpStatus.CREATED).body(cashbackTariffService.createTariff());
    }

    @GetMapping("/{id}")
    public CashbackTariffDto getTariffById(@PathVariable Long id) {
        log.info("Get Tariff by id: {}", id);
        return cashbackTariffService.getTariffById(id);
    }

    @PostMapping("/{id}/mappings")
    public void addMapping(@PathVariable Long id, @RequestBody CashbackMappingDto mappingDto) {
        log.info("Add Mapping: {}", mappingDto);
        cashbackTariffService.addMapping(id, mappingDto);
    }

    @PutMapping("/{id}/mappings")
    public void updateMapping(@PathVariable Long id, @RequestBody CashbackMappingDto mappingDto) {
        log.info("Update Mapping: {}", mappingDto);
        cashbackTariffService.updateMapping(id, mappingDto);
    }

    @DeleteMapping("/{id}/mappings")
    public void deleteMapping(@PathVariable Long id, @RequestParam String mappingKey) {
        log.info("Delete Mapping: {}", mappingKey);
        cashbackTariffService.deleteMapping(id, mappingKey);
    }
}