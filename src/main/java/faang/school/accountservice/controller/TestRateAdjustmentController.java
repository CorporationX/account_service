package faang.school.accountservice.controller;

import faang.school.accountservice.service.RateAdjustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestRateAdjustmentController {

    @Autowired
    private RateAdjustmentService rateAdjustmentService;

    @PostMapping("/adjust-rate")
    public ResponseEntity<String> adjustRate(
            @RequestParam("id") long id,
            @RequestParam("rateChange") double rateChange) {
        // Вызов метода сервиса с данными из запроса
        rateAdjustmentService.adjustRate(id, rateChange);

        // Возврат успешного ответа
        return ResponseEntity.ok("Rate adjustment completed successfully");
    }
}
