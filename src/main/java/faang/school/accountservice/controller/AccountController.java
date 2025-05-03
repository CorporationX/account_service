package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountOpenRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.dto.BalanceUpdateRequest;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {
    private AccountService accountService;

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

    @PutMapping("/{accountNumber}/block")
    public ResponseEntity<Void> block(@PathVariable String accountNumber) {
        accountService.block(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{accountNumber}/unblock")
    public ResponseEntity<Void> unblock(@PathVariable String accountNumber) {
        accountService.unblock(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{accountNumber}/close")
    public ResponseEntity<Void> close(@PathVariable String accountNumber) {
        accountService.close(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{accountNumber}/delete")
    public ResponseEntity<Void> delete(@PathVariable String accountNumber) {
        accountService.delete(accountNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{accountNumber}/balance")
    public ResponseEntity<Void  > updateBalance(@PathVariable String accountNumber,
                                                @Valid @RequestBody BalanceUpdateRequest request) {
        accountService.updateBalance(accountNumber, request.amount());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
