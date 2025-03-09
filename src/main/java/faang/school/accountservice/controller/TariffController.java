package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.service.savings.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public TariffDto createTariff(@RequestParam String name, @RequestParam List<Double> rates) {
        return tariffService.addTariff(name, rates);
    }

    @PatchMapping("/{id}/rate")
    public TariffDto updateTariff(@PathVariable Long id, @RequestParam double newRate) {
        return tariffService.updateTariffRate(id, newRate);
    }

    @GetMapping
    public List<TariffDto> getAllTariffs() {
        return tariffService.getAllTariffs();
    }

    @GetMapping("/{id}")
    public TariffDto getTariffById(@PathVariable Long id) {
        return tariffService.getTariffById(id);
    }
}
