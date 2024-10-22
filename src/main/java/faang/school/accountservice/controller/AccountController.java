package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.service.account.AccountService;
import lombok.RequiredArgsConstructor;
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
    private final AccountMapper mapper;

    @PostMapping
    public AccountDto openAccount(@RequestBody OpenAccountDto dto) {
        Account account = mapper.toEntity(dto);
        Account createdAccount = accountService.openAccount(account);

        return mapper.toDto(createdAccount);
    }

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);

        return mapper.toDto(account);
    }

    @PutMapping("/{id}/close")
    public AccountDto closeAccount(@PathVariable Long id) {
        Account account = accountService.closeAccount(id);

        return mapper.toDto(account);
    }
}
