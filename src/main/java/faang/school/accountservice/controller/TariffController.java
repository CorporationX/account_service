package faang.school.accountservice.controller;

import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.service.TariffService;
import faang.school.accountservice.mapper.TariffMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tariffs")
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;
    private final TariffMapper tariffMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffDto createTariff(@RequestParam String tariffType, @RequestParam Double rateValue) {
        return tariffMapper.toTariffDto(tariffService.createTariff(tariffType, rateValue));
    }

    @PutMapping("/{tariffId}")
    public TariffDto updateTariffRate(@PathVariable UUID tariffId, @RequestParam Double newRateValue) {
        return tariffMapper.toTariffDto(tariffService.updateTariffRate(tariffId, newRateValue));
    }

    @GetMapping("/{tariffId}")
    public TariffDto getTariffById(@PathVariable UUID tariffId) {
        return tariffMapper.toTariffDto(tariffService.getTariffById(tariffId));
    }
}