package faang.school.accountservice.controller.cahsback;

import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.ReadCashbackTariffDto;
import faang.school.accountservice.service.CashbackTariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cashback-tariffs")
@RequiredArgsConstructor
public class CashbackTariffController {
    private final CashbackTariffService cashbackTariffService;

    @GetMapping("/{tariffId}")
    public ReadCashbackTariffDto getCashbackTariff(@PathVariable long tariffId) {
        return cashbackTariffService.getTariff(tariffId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadCashbackTariffDto createCashbackTariff(@RequestBody CreateCashbackTariffDto createCashbackTariffDto) {
        return cashbackTariffService.createTariff(createCashbackTariffDto);
    }
}
