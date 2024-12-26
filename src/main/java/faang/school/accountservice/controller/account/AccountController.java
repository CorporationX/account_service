package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public AccountDto get(@PathVariable Long id) {
        return accountService.get(id);
    }

    @PostMapping
    public AccountDto openAccount(@RequestBody @Valid AccountDto account) {
        return accountService.openAccount(account);
    }

    @PostMapping("/{id}/block")
    public AccountDto freezeAccount(@PathVariable Long id) {
        return accountService.freezeAccount(id);
    }

    @PostMapping("/{id}/close")
    public AccountDto closeAccount(@PathVariable Long id) {
        return accountService.closeAccount(id);
    }

    @GetMapping("/users/{userId}")
    public List<AccountDto> getAllOfUser(@PathVariable Long userId) {
        return accountService.getAllOfUser(userId);
    }

    @GetMapping("/projects/{projectId}")
    public List<AccountDto> getAllOfProject(@PathVariable Long projectId) {
        return accountService.getAllOfProject(projectId);
    }
}