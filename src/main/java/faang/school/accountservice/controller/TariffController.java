package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffResponse;
import faang.school.accountservice.dto.TariffUpdateRequest;
import faang.school.accountservice.service.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tariffs")
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public ResponseEntity<TariffResponse> addTariff(@RequestParam String typeName) {
        TariffResponse response = tariffService.addTariff(typeName);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping
    public ResponseEntity<String> updateTariff(@Valid @RequestBody TariffUpdateRequest request) {
        tariffService.updateTariff(request);
        return ResponseEntity.status(HttpStatus.OK).body("Tariff updated successfully");
    }

    @GetMapping
    public List<TariffResponse> getAllTariffs() {
        return tariffService.getAllTariffs();
    }
}
