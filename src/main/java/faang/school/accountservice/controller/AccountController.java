package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    public AccountDto get(@PathVariable UUID id) {
        return accountService.get(id);
    }

    @PostMapping("/open")
    public AccountDto open(@RequestBody AccountDto request) {
        return accountService.open(request);
    }

    @PostMapping("/{id}/block")
    public AccountDto block(@PathVariable UUID id) {
        return accountService.block(id);
    }

    @PostMapping("/{id}/close")
    public AccountDto close(@PathVariable UUID id) {
        return accountService.close(id);
    }
}
