package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService service;

    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable Long id) {
        return service.getAccount(id);
    }

    @PostMapping("/open")
    public AccountDto openAccount(@RequestBody AccountDto accountDto) {
        return service.openAccount(accountDto);
    }

    @PatchMapping("/{id}/block")
    public AccountDto blockAccount(@PathVariable Long id) {
        return service.blockAccount(id);
    }

    @PatchMapping("/{id}/close")
    public AccountDto closeAccount(@PathVariable Long id) {
        return service.closeAccount(id);
    }
}
