package faang.school.accountservice.controller;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.dto.PaymentResponseDto;
import faang.school.accountservice.service.payment.PaymentOperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("accounts/")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentOperationService paymentOperationService;

    @PostMapping("payments/authorization")
    public PaymentResponseDto authorizePayment(@Valid @RequestBody PaymentOperationDto paymentOperationDto) {
        return paymentOperationService.authorizePayment(paymentOperationDto);
    }
}
