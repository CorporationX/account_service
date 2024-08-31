package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@RestController
@RequestMapping("v1/tariff")
@RequiredArgsConstructor
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTariff(@RequestBody @Validated TariffDto tariffDto) {
        tariffService.createTariff(tariffDto);
    }

    @PutMapping
    public void updateTariff(@RequestBody @Validated TariffDto tariffDto) {
        tariffService.updateTariff(tariffDto);
    }

    @GetMapping
    public List<TariffDto> getAllTariffs() {
        return tariffService.getAllTariffs();
    }
}
