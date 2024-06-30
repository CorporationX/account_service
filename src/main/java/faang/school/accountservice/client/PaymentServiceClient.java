package faang.school.accountservice.client;

import faang.school.accountservice.dto.payment.InvoiceDto;
import faang.school.accountservice.dto.payment.PaymentDto;
import faang.school.accountservice.dto.payment.PaymentRequest;
import faang.school.accountservice.dto.payment.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {
    @PostMapping("/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto);

    @PostMapping("/create")
    ResponseEntity<PaymentDto> createPayment(@RequestBody @Validated InvoiceDto invoiceDto);

    @PostMapping("/cancel/{id}")
    ResponseEntity<PaymentDto> cancelPayment(@PathVariable Long id);

    @PostMapping("/clear/{id}")
    ResponseEntity<PaymentDto> clearPayment(@PathVariable Long id);
}