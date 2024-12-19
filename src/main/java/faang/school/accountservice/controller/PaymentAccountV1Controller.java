package faang.school.accountservice.controller;

import faang.school.accountservice.dto.paymentAccount.CreatePaymentAccountDto;
import faang.school.accountservice.dto.paymentAccount.PaymentAccountDto;
import faang.school.accountservice.service.PaymentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-accounts")
@Validated
public class PaymentAccountV1Controller {
    private final PaymentAccountService paymentAccountService;

    @GetMapping("/{account_number}")
    public PaymentAccountDto getPaymentAccount(@PathVariable("account_number") String accountNumber) {
        return paymentAccountService.getPaymentAccountByNumber(accountNumber);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentAccountDto createPaymentAccount(@Validated @RequestBody CreatePaymentAccountDto createPaymentAccountDto) {
        return paymentAccountService.openPaymentAccount(createPaymentAccountDto);
    }

    @PutMapping("/{account_number}/close")
    public PaymentAccountDto closePaymentAccount(@PathVariable("account_number") String accountNumber) {
        return paymentAccountService.closePaymentAccount(accountNumber);
    }

    @PutMapping("/{account_number}/block")
    public PaymentAccountDto blockPaymentAccount(@PathVariable("account_number") String accountNumber) {
        return paymentAccountService.blockPaymentAccount(accountNumber);
    }

}
