package faang.school.accountservice.controller.tariff;

import faang.school.accountservice.dto.tariff.CreateTariffDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.UpdateTariffDto;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.service.tariff.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tariffs")
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public TariffDto createTariff(@RequestBody CreateTariffDto tariffDto) {
        return tariffService.createTariff(tariffDto);
    }

    @PutMapping
    public TariffDto updateTariffRate(@RequestBody UpdateTariffDto tariffDto) {
        return tariffService.updateTariffRate(tariffDto);
    }

    @GetMapping("/{id}")
    public TariffDto getTariff(@PathVariable Long id) {
        return tariffService.getTariff(id);
    }
}
