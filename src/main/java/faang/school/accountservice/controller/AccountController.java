package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.PaymentStatus;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

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

    @PostMapping("/spend/{accountId}")
    public DeferredResult<PaymentStatus> processPayment(@PathVariable Long accountId, @RequestParam BigDecimal sum) {
        return processRequest(accountService.processPayment(accountId, sum));
    }

    @PostMapping("/spend/authorized/{accountId}")
    public DeferredResult<PaymentStatus> spendFromAuthorizedBalance(@PathVariable Long accountId, @RequestParam BigDecimal sum) {
        return processRequest(accountService.spendFromAuthorizedBalance(accountId, sum));
    }

    @PostMapping("/receive/{accountId}")
    public DeferredResult<PaymentStatus> receiveFunds(@PathVariable Long accountId, @RequestParam BigDecimal sum) {
        return processRequest(accountService.receiveFunds(accountId, sum));
    }

    @PostMapping("/suspend/{accountId}")
    public DeferredResult<PaymentStatus> transferToAuthorizedBalance(@PathVariable Long accountId, @RequestParam BigDecimal sum) {
        return processRequest(accountService.transferToAuthorizedBalance(accountId, sum));
    }

    private DeferredResult<PaymentStatus> processRequest(CompletableFuture<PaymentStatus> future) {
        DeferredResult<PaymentStatus> deferredResult = new DeferredResult<>();
        future.thenAccept(deferredResult::setResult)
                .exceptionally(ex -> {
                    deferredResult.setErrorResult(HttpStatus.INTERNAL_SERVER_ERROR);
                    return null;
                });
        return deferredResult;
    }
}
