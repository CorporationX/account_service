package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffAndRateDto;
import faang.school.accountservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tariff")
public class TariffController {
    private final TariffService tariffService;

    @PatchMapping
    public TariffAndRateDto addTariffRate(@RequestParam("number") String accountNumber,
                                          @RequestParam("rate") Double tariffRate){
        return tariffService.addTariffRate(accountNumber, tariffRate);
    }

    @GetMapping
    public List<Double> getTariffRates(@RequestParam("number") String accountNumber){
        return tariffService.getTariffRates(accountNumber);
    }
}
