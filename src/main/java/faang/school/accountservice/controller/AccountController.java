package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountResponseDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper mapper;

    @PostMapping
    public AccountResponseDto openAccount(@RequestBody @Valid OpenAccountDto dto) {
        Account account = mapper.toEntity(dto);
        Account createdAccount = accountService.openAccount(account);

        return mapper.toResponseDto(createdAccount);
    }

    @GetMapping("/{id}")
    public AccountResponseDto getAccountById(@PathVariable UUID id) {
        Account account = accountService.getAccountById(id);

        return mapper.toResponseDto(account);
    }

    @PutMapping("/{id}/close")
    public AccountResponseDto closeAccount(@PathVariable UUID id) {
        Account account = accountService.closeAccount(id);

        return mapper.toResponseDto(account);
    }

    @PutMapping("/{id}/block")
    public AccountResponseDto blockAccount(@PathVariable UUID id) {
        Account account = accountService.blockAccount(id);

        return mapper.toResponseDto(account);
    }
}
