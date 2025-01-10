package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.service.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/tariff")
@RequiredArgsConstructor
@Validated
public class TariffController {
    private final TariffService tariffService;

    @GetMapping("/{id}")
    public TariffDto getTariff(@PathVariable Long id) {
        log.info("Received a request to get a tariff with ID: {}", id);
        return tariffService.getTariff(id);
    }

    @PostMapping()
    public TariffDto createTariff(@RequestBody @Valid TariffDto tariffDto) {
        log.info("Received a request to create a tariff");
        return tariffService.createTariff(tariffDto);
    }

}
