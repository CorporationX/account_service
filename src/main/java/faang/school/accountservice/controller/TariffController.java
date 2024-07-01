package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CreateTariffRequest;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.UpdateTariffRequest;
import faang.school.accountservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tariffs")
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;

    @PostMapping
    public TariffDto createTariff(@RequestBody CreateTariffRequest request){
        return tariffService.createTariff(request);
    }

    @PutMapping("/{id}")
    public TariffDto updateTariff(@PathVariable Long id, @RequestBody UpdateTariffRequest request){
        return tariffService.updateTariff(id, request);
    }

    @GetMapping("/{id}")
    public TariffDto getTariffById(@PathVariable Long id){
        return tariffService.getTariffById(id);
    }
}
