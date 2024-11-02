package faang.school.accountservice.controller.tariff;

import faang.school.accountservice.dto.tariff.TariffDto;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.service.tariff.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public TariffDto createTariff(@RequestBody @Valid TariffRequestDto tariffRequestDto) {
        return tariffService.createTariff(tariffRequestDto);
    }

    @GetMapping
    public List<TariffDto> getAllTariffs() {
        return tariffService.getAllTariffs();
    }

    @PutMapping
    public TariffDto updateTariffRate(@RequestBody @Valid TariffRequestDto tariffRequestDto) {
        return tariffService.updateTariffRate(tariffRequestDto);
    }
}
