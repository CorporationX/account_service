package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}/balance")
    public ResponseEntity<Long> getAccountBalance(@PathVariable Long id) {
        return ResponseEntity.ok(1000L);
    }

    @PostMapping
    public AccountDto create(@RequestBody @Valid CreateAccountDto createAccountDto) {
        return accountService.create(createAccountDto);
    }

    @GetMapping("/{id}")
    public AccountDto get(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping("{id}/block")
    public AccountDto block(@PathVariable Long id) {
        return accountService.updateAccountStatus(id, AccountStatus.FROZEN);
    }

    @PostMapping("{id}/close")
    public AccountDto close(@PathVariable Long id) {
        return accountService.updateAccountStatus(id, AccountStatus.CLOSED);
    }
}
