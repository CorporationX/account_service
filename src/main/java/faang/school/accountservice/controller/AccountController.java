package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.PaymentStatus;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account/")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("")
    public AccountDto get(@Validated @RequestBody AccountDto accountDto) {
        return accountService.getAccount(accountDto);
    }

    @PostMapping("")
    public AccountDto open(@Validated @RequestBody AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PostMapping("block")
    public AccountDto block(@Validated @RequestBody AccountDto accountDto) {
        return accountService.suspendAccount(accountDto);
    }

    @PostMapping("close")
    public AccountDto close(@Validated @RequestBody AccountDto accountDto) {
        return accountService.closeAccount(accountDto);
    }

    @PostMapping("spend/{accountId}")
    public DeferredResult<ResponseEntity<PaymentStatus>> processPayment(@PathVariable Long accountId, BigDecimal sum) {
        DeferredResult<ResponseEntity<PaymentStatus>> deferredResult = new DeferredResult<>();

        accountService.processPayment(accountId, sum)
                .thenAccept(status -> deferredResult.setResult(ResponseEntity.ok(status)))
                .exceptionally(ex -> {
                    deferredResult.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));
                    return null;
                });

        return deferredResult;
    }

    @PostMapping("/spend/a/{accountId}")
    public DeferredResult<ResponseEntity<PaymentStatus>> spendFromAuthorizedBalance(@PathVariable Long accountId, @RequestParam BigDecimal sum) {
        DeferredResult<ResponseEntity<PaymentStatus>> deferredResult = new DeferredResult<>();

        accountService.spendFromAuthorizedBalance(accountId, sum)
                .thenAccept(status -> deferredResult.setResult(ResponseEntity.ok(status)))
                .exceptionally(ex -> {
                    deferredResult.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));
                    return null;
                });

        return deferredResult;
    }

    @PostMapping("/suspend/{accountId}")
    public DeferredResult<ResponseEntity<PaymentStatus>> suspendBalance(@PathVariable Long accountId, @RequestParam BigDecimal sum) {
        DeferredResult<ResponseEntity<PaymentStatus>> deferredResult = new DeferredResult<>();

        accountService.suspendBalance(accountId, sum)
                .thenAccept(status -> deferredResult.setResult(ResponseEntity.ok(status)))
                .exceptionally(ex -> {
                    deferredResult.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));
                    return null;
                });

        return deferredResult;
    }

    @PostMapping("/receive/{accountId}")
    public DeferredResult<ResponseEntity<PaymentStatus>> receiveFunds(@PathVariable Long accountId, @RequestParam BigDecimal sum) {
        DeferredResult<ResponseEntity<PaymentStatus>> deferredResult = new DeferredResult<>();

        accountService.receiveFunds(accountId, sum)
                .thenAccept(status -> deferredResult.setResult(ResponseEntity.ok(status)))
                .exceptionally(ex -> {
                    deferredResult.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));
                    return null;
                });

        return deferredResult;
    }
}
