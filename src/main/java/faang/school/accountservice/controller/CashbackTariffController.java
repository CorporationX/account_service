package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.TypeMappingDto;
import faang.school.accountservice.service.CashbackTariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CashbackTariffController {
    private final CashbackTariffService cashbackTariffService;

    @GetMapping("/tariff/{id}")
    public TariffDto getTariffById(@PathVariable Long id) {
        return cashbackTariffService.getTariff(id);
    }

    @PostMapping("/tariff/create")
    public TariffDto createTariff(@RequestBody TariffDto tariffDto) {
        return cashbackTariffService.createTariff(tariffDto);
    }

    @PutMapping("/tariff/{id}")
    public TariffDto updateTariff(@RequestBody TariffDto tariffDto, @PathVariable Long id) {
        return cashbackTariffService.updateTariff(tariffDto, id);
    }

    @DeleteMapping("/tariff/{id}")
    public void deleteTariff(@PathVariable long id) {
        cashbackTariffService.deleteTariff(id);
    }

    @GetMapping("/cashback_mapping")
    public TypeMappingDto getCashbackMapping(@RequestBody TypeMappingDto typeMappingDto) {
        return cashbackTariffService.getCashbackMapping(typeMappingDto);
    }

    @PostMapping("/cashback/create")
    public TypeMappingDto createCashbackMapping(@Valid @RequestBody TypeMappingDto typeMappingDto) {
        return cashbackTariffService.createCashbackMapping(typeMappingDto);
    }

    @PutMapping("/cashback/update")
    public void updateCashbackMapping(@Valid @RequestBody TypeMappingDto typeMappingDto) {
        cashbackTariffService.updateCashbackMapping(typeMappingDto);
    }

    @DeleteMapping("/cashback/delete")
    public void deleteCashbackMapping(@RequestBody TypeMappingDto typeMappingDto) {
        cashbackTariffService.deleteCashbackMapping(typeMappingDto);
    }
}
