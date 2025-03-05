package faang.school.accountservice.controller;

import faang.school.accountservice.dto.tariff.AddRateTariffRequest;
import faang.school.accountservice.dto.tariff.CreateTariffRequest;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.service.TariffService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tariff")
@RequiredArgsConstructor
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public TariffDto createTariff(@Valid @RequestBody CreateTariffRequest createTariffRequest) {
        return tariffService.addTariff(createTariffRequest);
    }

    @PatchMapping("/rate")
    public TariffDto addRate(@Valid @RequestBody AddRateTariffRequest addRateTariffRequest) {
        return tariffService.addRate(addRateTariffRequest);
    }

    @GetMapping("/rate/{tariffId}")
    public BigDecimal getActualRate(@Valid @NotNull @Positive @PathVariable Long tariffId){
        return tariffService.getActualRate(tariffId);
    }

    @GetMapping("/{tariffId}")
    public TariffDto getTariffById(@Valid @NotNull @Positive @PathVariable Long tariffId) {
        return tariffService.getTariffDtoById(tariffId);
    }

    @GetMapping("/all")
    public List<TariffDto> getTariffs() {
        return tariffService.getTariffsDto();
    }
}
