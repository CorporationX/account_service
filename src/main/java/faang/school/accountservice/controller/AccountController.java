package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping()
    public AccountDto getAccount(@RequestParam("number") String accountNumber){
        return accountService.getAccountByNumber(accountNumber);
    }

    @PostMapping()
    public AccountDto openAccount(@RequestBody AccountDto accountDto){
        return accountService.openAccount(accountDto);
    }

    @PatchMapping("/block")
    public void blockAccount(@RequestParam("number") String accountNumber){
        accountService.blockAccount(accountNumber);
    }

    @DeleteMapping()
    public void closeAccount(@RequestParam("number") String accountNumber){
        accountService.closeAccount(accountNumber);
    }
}
