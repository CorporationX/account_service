package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.service.account.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffDto createTariff(@RequestBody @Valid TariffDto tariffDto,
                                  @RequestParam String rate) {

        return tariffService.createTariff(tariffDto, rate);
    }

    @PutMapping("/{id}/history")
    @ResponseStatus(HttpStatus.OK)
    public TariffDto updateTariffHistory(@PathVariable UUID id,
                                         @RequestParam String rate) {
        return tariffService.updateTariffHistory(id, rate);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TariffDto getTariffById(@PathVariable UUID id) {
        return tariffService.getTariffById(id);
    }

    @GetMapping("/{id}/history")
    @ResponseStatus(HttpStatus.OK)
    public String getRateHistoryById(@PathVariable UUID id) {
        return tariffService.getRateHistoryById(id);
    }

    @GetMapping("/{id}/rate")
    @ResponseStatus(HttpStatus.OK)
    public String getRateByById(@PathVariable UUID id) {
        return tariffService.getRateByById(id);
    }
}
