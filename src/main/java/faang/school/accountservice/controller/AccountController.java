package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
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

    @GetMapping("/all/user/{userId}")
    public List<AccountDto> getAllOfUser(@PathVariable Long userId) {
        return accountService.getAllOfUser(userId);
    }

    @GetMapping("/all/project/{projectId}")
    public List<AccountDto> getAllOfProject(@PathVariable Long projectId) {
        return accountService.getAllOfProject(projectId);
    }
}