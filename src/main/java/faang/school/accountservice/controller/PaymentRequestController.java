package faang.school.accountservice.controller;

import faang.school.accountservice.service.PaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/requests/payments")
@RequiredArgsConstructor
public class PaymentRequestController {

    private final PaymentRequestService paymentRequestService;

    @GetMapping("/{token}")
    public void findPaymentRequestByToken(@PathVariable UUID token) {
        paymentRequestService.findPaymentRequestByToken(token);
    }
}
