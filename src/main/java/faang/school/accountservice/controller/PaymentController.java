package faang.school.accountservice.controller;

import faang.school.accountservice.dto.PaymentResultDto;
import faang.school.accountservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResultDto payment(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return paymentService.paymentProcess(accountNumber, amount);
    }

}
