package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.service.TariffService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tariff")
@Validated
public class TariffController {
    private final TariffService tariffService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public TariffDto createTariff(@RequestBody @Validated(TariffDto.Create.class) TariffDto tariffDto) {
        return tariffService.createTariff(tariffDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public TariffDto updateTariffById(@Positive @PathVariable Long id, @RequestParam BigDecimal rate) {
        return tariffService.updateTariff(id, rate);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public TariffDto getTariffById(@Positive @PathVariable Long id) {
        return tariffService.getTariff(id);
    }
}
