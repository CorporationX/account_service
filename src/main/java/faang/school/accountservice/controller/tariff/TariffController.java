package faang.school.accountservice.controller.tariff;

import faang.school.accountservice.dto.CreateTariffRequest;
import faang.school.accountservice.dto.UpdateTariffRateRequest;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.service.tariff.TariffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tariffs")
public class TariffController {

    private TariffService tariffService;

    @PostMapping
    public Tariff createTariff(@RequestBody CreateTariffRequest request) {
        return tariffService.createTariff(request.getName(), request.getRateHistory());
    }

    @PutMapping("/{id}/rate")
    public Tariff updateTariffRate(@PathVariable Long id, @RequestBody UpdateTariffRateRequest request) {
        return tariffService.updateTariffRate(id, request.getNewRate());
    }

    @GetMapping("/{id}")
    public Tariff getTariff(@PathVariable Long id) {
        return tariffService.getTariff(id);
    }
}
