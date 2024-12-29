package faang.school.accountservice.controller.tariff;

import faang.school.accountservice.dto.tariff.TariffCreateDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.service.tariff.TariffService;
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

@RestController
@RequestMapping("/tariffs")
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;

    @GetMapping
    public List<TariffDto> getAllTariffs() {
        return tariffService.getAllTariffs();
    }

    @GetMapping("/{id}")
    public TariffDto getById(@PathVariable Long id) {
        return tariffService.findById(id);
    }

    @PostMapping
    public TariffDto createTariff(@Valid @RequestBody TariffCreateDto createDto) {
        return tariffService.createTariff(createDto);
    }

    @PatchMapping
    public TariffDto updateTariff(@Valid @RequestBody TariffUpdateDto updateDto) {
        return tariffService.updateTariff(updateDto);
    }


}
