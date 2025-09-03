package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.service.TariffService;
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
    private final TariffService service;

    @PostMapping
    public TariffDto create(@RequestBody @Valid TariffDto tariffDto) {
        return service.create(tariffDto);
    }

    @PutMapping
    public TariffDto update(@RequestBody @Valid TariffDto tariffDto) {
        return service.update(tariffDto);
    }

    @GetMapping
    public List<TariffDto> findAll() {
        return service.findAll();
    }
}
