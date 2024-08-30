package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{Id}")
    public AccountDto getAccount(@Positive @PathVariable Long id){
        return accountService.getAccount(id);
    }

    @GetMapping("/get_by_number/{number}")
    public AccountDto getAccountByNumber(@PathVariable String number){
        return accountService.getAccountByNumber(number);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@Valid @RequestBody OpenAccountDto openAccountDto){
        return accountService.openAccount(openAccountDto);
    }

    @PutMapping("/block/{Id}")
    public AccountDto blockAccount(@Positive @PathVariable Long id){
        return accountService.blockAccount(id);
    }

    @PutMapping("/close/{Id}")
    public AccountDto closeAccount(@Positive @PathVariable Long id){
        return accountService.closeAccount(id);
    }
}
