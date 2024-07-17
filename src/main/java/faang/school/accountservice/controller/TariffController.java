package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffDto createTariff(String name, List<Double> rateHistory) {
        return tariffService.createTariff(name, rateHistory);
    }

    @PutMapping("/{tariffId}")
    public TariffDto updateTariff(@PathVariable Long tariffId,
                                  @RequestParam Double rate) {
        return tariffService.updateTariff(tariffId, rate);
    }

    @GetMapping("/{tariffId}")
    public TariffDto getTariffById(@PathVariable Long tariffId) {
        return tariffService.getTariffById(tariffId);
    }
}
