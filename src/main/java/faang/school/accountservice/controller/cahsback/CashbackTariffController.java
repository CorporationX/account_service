package faang.school.accountservice.controller.cahsback;

import faang.school.accountservice.model.cashback.CreateCashbackTariffDto;
import faang.school.accountservice.model.cashback.ReadCashbackTariffDto;
import faang.school.accountservice.service.CashbackTariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
