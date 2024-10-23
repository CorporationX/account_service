package faang.school.accountservice.controller;

import faang.school.accountservice.dto.PaymentAccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class PaymentAccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public PaymentAccountDto getPaymentAccount(@PathVariable Long id) {
        return accountService.getPaymentAccount(id);
    }

    @PostMapping("/create")
    public PaymentAccountDto createPaymentAccount(@RequestBody PaymentAccountDto paymentAccountDto) {
        return accountService.createPaymentAccount(paymentAccountDto);
    }

    @PutMapping("/{id}")
    public PaymentAccountDto updatePaymentAccount(@RequestBody PaymentAccountDto paymentAccountDto, @PathVariable Long id) {
        return accountService.updatePaymentAccount(paymentAccountDto);
    }

    @DeleteMapping("/{id}")
    public void deletePaymentAccount(@PathVariable Long id) {
        accountService.deletePaymentAccount(id);
    }
}
