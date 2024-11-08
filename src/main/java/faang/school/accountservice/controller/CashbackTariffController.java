package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.dto.CashbackMappingDto;
import faang.school.accountservice.service.cashback.CashbackTariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CashbackTariffController {
    private final CashbackTariffService cashbackTariffService;

    @GetMapping("/tariff/{id}")
    public CashbackTariffDto getTariffById(@PathVariable Long id) {
        return cashbackTariffService.getTariff(id);
    }

    @PostMapping("/tariff/create")
    public CashbackTariffDto createTariff(@RequestBody CashbackTariffDto cashbackTariffDto) {
        return cashbackTariffService.createTariff(cashbackTariffDto);
    }

    @PutMapping("/tariff/{id}")
    public CashbackTariffDto updateTariff(@RequestBody CashbackTariffDto cashbackTariffDto, @PathVariable Long id) {
        return cashbackTariffService.updateTariff(cashbackTariffDto, id);
    }

    @DeleteMapping("/tariff/{id}")
    public void deleteTariff(@PathVariable long id) {
        cashbackTariffService.deleteTariff(id);
    }

    @GetMapping("/cashback_mapping")
    public CashbackMappingDto getCashbackMapping(@RequestBody CashbackMappingDto cashbackMappingDto) {
        return cashbackTariffService.getCashbackMapping(cashbackMappingDto);
    }

    @PostMapping("/cashback_mapping")
    public CashbackMappingDto createCashbackMapping(@Valid @RequestBody CashbackMappingDto cashbackMappingDto) {
        return cashbackTariffService.createCashbackMapping(cashbackMappingDto);
    }

    @PutMapping("/cashback_mapping")
    public void updateCashbackMapping(@Valid @RequestBody CashbackMappingDto cashbackMappingDto) {
        cashbackTariffService.updateCashbackMapping(cashbackMappingDto);
    }

    @DeleteMapping("/cashback_mapping")
    public void deleteCashbackMapping(@RequestBody CashbackMappingDto cashbackMappingDto) {
        cashbackTariffService.deleteCashbackMapping(cashbackMappingDto);
    }
}
