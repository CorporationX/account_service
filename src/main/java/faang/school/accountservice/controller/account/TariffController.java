package faang.school.accountservice.controller.account;

import faang.school.accountservice.controller.ApiPath;
import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.service.account.TariffService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.TARIFF_PATH)
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public ResponseEntity<TariffDto> createTariff(@RequestBody @Valid TariffDto tariffDto) {
        var tariff = tariffService.createTariff(tariffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tariff);
    }

    @PutMapping("/{tariffId}")
    public ResponseEntity<Object> updateTariff(@PathVariable("tariffId") Long id, @RequestBody @Valid TariffDto tariffDto) throws Exception {
        try {
            var updatedTariffDto = tariffService.updateTariff(id, tariffDto);
            return ResponseEntity.ok(updatedTariffDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<TariffDto> getTariffs() {
        return tariffService.getAllTariffs();
    }
}
