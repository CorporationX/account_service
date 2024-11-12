package faang.school.accountservice.controller;

import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@Validated
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PutMapping("/cancel")
    public PendingDto cancelPayment(@RequestBody PendingDto pendingDto) {
        log.info("Cancelling payment for {}", pendingDto);
        return paymentService.cancelPayment(pendingDto);
    }

    @PutMapping("/clearing")
    public PendingDto clearPayment(@RequestBody PendingDto pendingDto) {
        log.info("Clearing payment for {}", pendingDto);
        return paymentService.clearingPayment(pendingDto);
    }
}
