package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.service.CashbackTariffService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tariff/cashback")
@RequiredArgsConstructor
public class CashbackTariffController {

    private final CashbackTariffService tariffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CashbackTariffDto createCashbackTariff(@Validated @RequestBody CashbackTariffDto cashbackTariffDto) {
        return tariffService.createCashbackTariff(cashbackTariffDto);
    }

    @GetMapping("/{id}")
    public CashbackTariffDto getCashbackTariffById(@PathVariable @Positive Long id) {
        return tariffService.getCashbackTariffById(id);
    }
}
