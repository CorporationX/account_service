package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
