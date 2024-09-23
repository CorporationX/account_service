package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.enums.PaymentStatus;
import faang.school.accountservice.exception.ActiveRequestException;
import faang.school.accountservice.exception.MoreThanOneActiveRequestAtATimeException;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account/")
@Slf4j
public class AccountController {

    private final AccountService accountService;


    @GetMapping("")
    public AccountDto get(@Validated @RequestBody AccountDto accountDto) {
        return accountService.getAccount(accountDto);
    }


//    public AccountDto open(@Validated @RequestBody AccountDto accountDto) {
//        return accountService.openAccount(accountDto);
//    }

    @PostMapping("")
    public AccountDto open(@RequestBody RequestDto requestDto) {
        openRequest(requestDto);
        return accountService.openAccount(requestDto);
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

    private void openRequest(RequestDto requestDto) {
        Long userId = requestDto.getUserId();
        if (accountService.existsByUserLock(userId, userId)) {
            log.info("User with ID {} has an active request.", userId);
            throw new MoreThanOneActiveRequestAtATimeException("You can't have more than one active request." +
                    "Please wait for it to complete.");
        }
        if (accountService.existsById(requestDto.getId())) {
            log.info("Request with ID {} is already open.", requestDto.getId());
            throw new ActiveRequestException("This request is being processed please wait.");
        } else {
            log.info("Creating a new request for user ID {} with request ID {}", userId, requestDto.getId());
            accountService.createRequest(requestDto);
        }
    }
}
