package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountBalanceResponse;
import faang.school.accountservice.dto.account.AccountOpenRequest;
import faang.school.accountservice.dto.account.AccountResponse;
import faang.school.accountservice.dto.account.BalanceUpdateRequest;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Void> open(@RequestBody AccountOpenRequest request) {
        accountService.open(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> get(@PathVariable String accountNumber) {
        AccountResponse response = accountService.get(accountNumber);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> get(@RequestParam Long ownerId, @RequestParam OwnerType ownerType) {
        List<AccountResponse> accountResponseList = accountService.get(ownerId, ownerType);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponseList);
    }

    @PatchMapping("/{accountNumber}/block")
    public ResponseEntity<Void> block(@PathVariable String accountNumber) {
        accountService.block(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{accountNumber}/unblock")
    public ResponseEntity<Void> unblock(@PathVariable String accountNumber) {
        accountService.unblock(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{accountNumber}/close")
    public ResponseEntity<Void> close(@PathVariable String accountNumber) {
        accountService.close(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{accountNumber}/delete")
    public ResponseEntity<Void> delete(@PathVariable String accountNumber) {
        accountService.delete(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{accountNumber}/balance")
    public ResponseEntity<AccountBalanceResponse> updateBalance(@PathVariable String accountNumber,
                                                                @Valid @RequestBody BalanceUpdateRequest request) {
        AccountBalanceResponse response = accountService.updateBalance(accountNumber, request.amount());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
