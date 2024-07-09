package faang.school.accountservice.controller;

import faang.school.accountservice.dto.CreateTariffRequest;
import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.dto.UpdateTariffRequest;
import faang.school.accountservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

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
    public ResponseEntity<Void> updateTariff(@PathVariable Long id, @RequestBody BigDecimal request){
        tariffService.updateTariff(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public TariffDto getTariffById(@PathVariable Long id){
        return tariffService.getTariffById(id);
    }
}
