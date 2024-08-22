package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts/")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("")
    public AccountDto openAccount(@Validated @RequestBody AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("close")
    public AccountDto closeAccount(@Validated @RequestBody AccountDto accountDto) {
        return accountService.closeAccount(accountDto);
    }

    @PutMapping("block")
    public AccountDto blockAccount(@Validated @RequestBody AccountDto accountDto) {
        return accountService.blockAccount(accountDto);
    }

    @GetMapping("")
    public AccountDto getAccount(@Validated @RequestBody AccountDto accountDto) {
        return accountService.getAccount(accountDto);
    }
}
