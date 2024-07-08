package faang.school.accountservice.client;

import faang.school.accountservice.dto.payment.InvoiceDto;
import faang.school.accountservice.dto.payment.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/create")
    ResponseEntity<PaymentResponse> createPayment(@RequestBody @Validated InvoiceDto invoiceDto);

    @PostMapping("/cancel/{id}")
    ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long id);

    @PostMapping("/clear/{id}")
    ResponseEntity<PaymentResponse> clearPayment(@PathVariable Long id);

    @PostMapping("/success/{id}")
    ResponseEntity<PaymentResponse> passPayment(@PathVariable Long id);
}