package faang.school.accountservice.controller.tariff;

import faang.school.accountservice.dto.tariff.TariffCreateDto;
import faang.school.accountservice.dto.tariff.TariffResponse;
import faang.school.accountservice.dto.tariff.TariffUpdateDto;
import faang.school.accountservice.service.tariff.TariffService;
import faang.school.accountservice.validator.tariff.ValidTariffUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tariffs")
@RequiredArgsConstructor
@Validated
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public TariffResponse createTariff(@Valid @RequestBody TariffCreateDto creationDto) {
        return tariffService.createTariff(creationDto);
    }

    @PatchMapping("/{tariffId}")
    public TariffResponse updateTariff(
            @NotNull @RequestBody @ValidTariffUpdateDto TariffUpdateDto updateDto,
            @PathVariable @Min(1) long tariffId)
    {
        return tariffService.updateTariff(tariffId, updateDto);
    }

    @GetMapping
    public List<TariffResponse> getAllTariffs() {
        return tariffService.getAllTariffs();
    }

    @DeleteMapping("/{tariffId}")
    public void deleteTariff(@PathVariable @Min(1) long tariffId) {
        tariffService.deleteTariff(tariffId);
    }
}
