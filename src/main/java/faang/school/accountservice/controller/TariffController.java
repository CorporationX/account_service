package faang.school.accountservice.controller;


import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.TariffRequestDto;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.service.TariffService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tariff")
@RequiredArgsConstructor
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffDto addTariff(@RequestBody @Valid TariffRequestDto requestDto) {
        return tariffService.addTariff(requestDto.getTariffDto(), requestDto.getSavingsAccountDto());
    }

    @PatchMapping("/{id}/{newRate}")
    public TariffDto changeTariffRate(@PathVariable @Positive Long id, @PathVariable @NotNull @Positive BigDecimal newRate) {
        return tariffService.changeRateTariff(id, newRate);
    }

    @GetMapping("/all")
    public List<TariffType> getAllTariffs() {
        return tariffService.getAllTariffs();
    }
}
