package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountCreateProjectDto;
import faang.school.accountservice.dto.account.AccountCreateUserDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.facade.AccountFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountFacade accountFacade;

    @PostMapping("/user")
    public ResponseEntity<ResponseAccountDto> createAccountForUser(@RequestBody AccountCreateUserDto accountDto) {
        ResponseAccountDto responseDto = accountFacade.createAccountForUser(accountDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/project")
    public ResponseEntity<ResponseAccountDto> createAccountForProject(@RequestBody AccountCreateProjectDto accountDto) {
        ResponseAccountDto responseDto = accountFacade.createAccountForProject(accountDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ResponseAccountDto> getAccountById(@PathVariable UUID accountId) {
        ResponseAccountDto accountDto = accountFacade.getAccountById(accountId);
        return ResponseEntity.ok(accountDto);
    }

    @GetMapping("/numbers/{accountNumber}")
    public ResponseEntity<ResponseAccountDto> getAccountByNumber(@PathVariable String accountNumber) {
        ResponseAccountDto accountDto = accountFacade.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(accountDto);
    }

    @PatchMapping("/close/{accountId}")
    public ResponseEntity<ResponseAccountDto> closeAccountStatus(@PathVariable UUID accountId) {
        ResponseAccountDto accountDto = accountFacade.closeAccount(accountId);
        return ResponseEntity.ok(accountDto);
    }

    @PatchMapping("/convert/{accountId}")
    public ResponseEntity<ResponseAccountDto> convertAccountCurrency(@PathVariable UUID accountId,
                                                                    @RequestParam Currency currency) {
        ResponseAccountDto accountDto = accountFacade.convertAccountCurrency(accountId, currency);
        return ResponseEntity.ok(accountDto);
    }
}
