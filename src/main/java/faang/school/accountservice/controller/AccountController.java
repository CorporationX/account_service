package faang.school.accountservice.controller;


import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/")
    public AccountDto open(@RequestBody @Valid AccountDto accountDto) {
        return accountService.open(accountDto);
    }

    @GetMapping("/{id}")
    public AccountDto get(@PathVariable("id") long accountId) {
        return accountService.get(accountId);
    }

    @PutMapping("/block/id")
    public void block(long accountId) {
        accountService.block(accountId);
    }

    @PutMapping("/close/id")
    public void close(long accountId) {
        accountService.close(accountId);
    }
}