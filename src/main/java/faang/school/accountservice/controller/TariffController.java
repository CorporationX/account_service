package faang.school.accountservice.controller;

import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RequestMapping(value = "api/v1/tariff")
@RestController
public class TariffController {
    private final TariffMapper tariffMapper;
    private final TariffService tariffService;

    @PostMapping
    public TariffResponseDto createTariff(@RequestBody TariffRequestDto tariffDto) {
        Tariff savedTariff = tariffService.create(tariffDto);
        return tariffMapper.toDto(savedTariff);

    }

    @PatchMapping
    public TariffResponseDto updateTariff(@RequestBody TariffUpdateDto tariffUpdate) {
        Tariff update = tariffService.update(tariffUpdate);
        return tariffMapper.toDto(update);
    }

    @GetMapping("/{tariffName}")
    public TariffResponseDto getTariff(@PathVariable TariffType tariffName) {
        Tariff getTariff = tariffService.getTariff(tariffName);
        return tariffMapper.toDto(getTariff);
    }
}
