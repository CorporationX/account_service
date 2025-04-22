package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getAccount(@PathVariable long id) {
        return accountService.getAccount(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@RequestBody @Valid AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("/block/{id}")
    public AccountDto blockAccount(@PathVariable long id) {
        return accountService.blockAccount(id);
    }

    @PutMapping("/close/{id}")
    public AccountDto closeAccount(@PathVariable long id) {
        return accountService.closeAccount(id);
    }
}
