package faang.school.accountservice.controller.tariff;

import faang.school.accountservice.dto.tariff.TariffCreationDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.service.tariff.TariffService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tariffs")
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;

    @PostMapping
    public ResponseEntity<TariffResponseDto> createTariff(
            @RequestBody @Valid TariffCreationDto tariffCreationDto) {
        TariffResponseDto response = tariffService.createTariff(tariffCreationDto);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{tariffId}/rate")
    public ResponseEntity<TariffResponseDto> updateTariffRate(
            @PathVariable Long tariffId,
            @RequestParam @NonNull BigDecimal newRate
    ) {
        TariffResponseDto response = tariffService.updateTariffRate(tariffId, newRate);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{tariffId}")
    public ResponseEntity<TariffResponseDto> getTariff(@PathVariable Long tariffId) {
        TariffResponseDto response = tariffService.getTariff(tariffId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<List<TariffResponseDto>> getAllTariffs() {
        List<TariffResponseDto> response = tariffService.getAllTariffs();
        return ResponseEntity.ok().body(response);
    }
}
