package faang.school.accountservice.controller.payment;

import faang.school.accountservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PutMapping("/auth/{paymentId}")
    public void authorizePayment(@PathVariable("paymentId") Long paymentId) {
        paymentService.authorizePayment(paymentId);
    }

    @GetMapping("/cancel/{paymentId}/user/{userId}")
    public void cancelTransaction(@PathVariable("paymentId") Long paymentId, @PathVariable("userId") Long userId) {
        paymentService.cancelPayment(paymentId, userId);
    }

    @GetMapping("/clear/{paymentId}")
    public void clearPayment(@PathVariable("paymentId") Long paymentId) {
        paymentService.clearPayment(paymentId);
    }
}

