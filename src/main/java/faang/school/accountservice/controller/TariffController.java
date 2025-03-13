package faang.school.accountservice.controller;

import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tariffs")
@RequiredArgsConstructor
public class TariffController {
    private static final Logger logger = LoggerFactory.getLogger(TariffController.class);
    private final TariffService tariffService;

    @PostMapping
    public ResponseEntity<Tariff> createTariff(@RequestParam String name, @RequestParam BigDecimal initialRate) {
        logger.info("Request received to create tariff with name: {} and initialRate: {}", name, initialRate);
        Tariff tariff = tariffService.createTariff(name, initialRate);
        logger.info("Tariff created successfully with ID: {}", tariff.getId());
        return ResponseEntity.ok(tariff);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tariff> updateTariffRate(@PathVariable Long id, @RequestParam BigDecimal newRate) {
        logger.info("Request received to update tariff ID: {} with newRate: {}", id, newRate);
        Tariff updatedTariff = tariffService.updateTariffRate(id, newRate);
        logger.info("Tariff updated successfully: {}", updatedTariff);
        return ResponseEntity.ok(updatedTariff);
    }

    @GetMapping
    public ResponseEntity<List<Tariff>> getAllTariffs() {
        logger.info("Request received to fetch all tariffs");
        List<Tariff> tariffs = tariffService.getAllTariffs();
        logger.info("Tariffs retrieved: {}", tariffs);
        return ResponseEntity.ok(tariffs);
    }
}
