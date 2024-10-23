package faang.school.accountservice.controller;

import faang.school.accountservice.dto.tariff.TariffCreateDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.dto.tariff.UpdateRateDto;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.model.enumeration.TariffType;
import faang.school.accountservice.service.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/tariffs")
@RestController
public class TariffController {
    private final TariffMapper tariffMapper;
    private final TariffService tariffService;

    @PostMapping
    public TariffResponseDto create(@RequestBody @Valid TariffCreateDto dto) {
        Tariff tariff = tariffMapper.toEntity(dto);
        Tariff createdTariff = tariffService.create(tariff, dto.getRate());
        return tariffMapper.toResponseDto(createdTariff);
    }

    @PatchMapping
    public TariffResponseDto updateRate(@RequestBody @Valid UpdateRateDto dto) {
        Tariff updatedTariff = tariffService.updateRate(dto.getTariffId(), dto.getNewRate());
        return tariffMapper.toResponseDto(updatedTariff);
    }

    @GetMapping("/id/{id}")
    public TariffResponseDto getById(@PathVariable Long id) {
        Tariff foundTariff = tariffService.findById(id);
        return tariffMapper.toResponseDto(foundTariff);
    }

    @GetMapping("/type/{type}")
    public TariffResponseDto getByType(@PathVariable TariffType type) {
        Tariff foundTariff = tariffService.findByType(type);
        return tariffMapper.toResponseDto(foundTariff);
    }

    @GetMapping
    public List<TariffResponseDto> getAll() {
        List<Tariff> foundTariffs = tariffService.findAll();
        return tariffMapper.toResponseDtos(foundTariffs);
    }
}
