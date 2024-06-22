package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.AccountDtoToUpdate;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/open")
    public AccountDto open(@RequestBody @Valid AccountCreateDto accountCreateDto) {
        return accountService.open(accountCreateDto);
    }

    @PutMapping("/{accountId}")
    public AccountDto update(@PathVariable("accountId") long accountId,
                             @RequestBody @Valid AccountDtoToUpdate accountDto) {
        return accountService.update(accountId, accountDto);
    }

    @GetMapping("/{accountId}")
    public AccountDto get(@PathVariable long accountId) {
        return accountService.get(accountId);
    }

    @PutMapping("/block/{accountId}")
    public void block(@PathVariable long accountId) {
        accountService.block(accountId);
    }

    @PutMapping("/unblock/{accountId}")
    public void unBlock(@PathVariable long accountId) {
        accountService.unBlock(accountId);
    }

    @DeleteMapping("/{accountId}")
    public void close(@PathVariable long accountId) {
        accountService.close(accountId);
    }
}