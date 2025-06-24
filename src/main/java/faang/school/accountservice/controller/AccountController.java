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

    @GetMapping("/{ownerId}")
    public AccountDto getAccount(@PathVariable Long ownerId) {
        return service.getAccount(ownerId);
    }

    @PostMapping("/open")
    public AccountDto openAccount(@RequestBody AccountDto accountDto) {
        return service.openAccount(accountDto);
    }

    @PatchMapping("/{ownerId}/block")
    public AccountDto blockAccount(@PathVariable Long ownerId) {
        return service.blockAccount(ownerId);
    }

    @PatchMapping("/{ownerId}/close")
    public AccountDto closeAccount(@PathVariable Long ownerId) {
        return service.closeAccount(ownerId);
    }
}
