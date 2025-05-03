package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/accounts")
@RestController
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public AccountResponseDto getAccountById(
            @Positive @PathVariable Long id) {

        Account account = accountService.getAccountById(id);
        return accountMapper.toDto(account);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/by-number/{accountNumber}")
    public AccountResponseDto getAccountByNumber(
            @Pattern(regexp = "\\d{12,20}", message = "Account number must be digits, length from 12 to 20")
            @PathVariable String accountNumber) {

        Account account = accountService.getAccountByNumber(accountNumber);
        return accountMapper.toDto(account);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<AccountResponseDto> getAccountsByOwner(
            @Positive @RequestParam("ownerId") Long ownerId,
            @RequestParam("ownerType") OwnerType ownerType) {

        List<Account> accounts = accountService.getAccountsByOwner(ownerId, ownerType);
        return accountMapper.toDtoList(accounts);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/open")
    public AccountResponseDto openAccount(@Valid @RequestBody AccountCreateDto dto) {
        Account account = accountMapper.toEntity(dto);
        Account savedAccount = accountService.openAccount(account);
        return accountMapper.toDto(savedAccount);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/block")
    public AccountResponseDto blockAccount(@Positive @PathVariable Long id) {
        Account account = accountService.blockAccount(id);
        return accountMapper.toDto(account);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/close")
    public AccountResponseDto closeAccount(@Positive @PathVariable Long id) {
        Account account = accountService.closeAccount(id);
        return accountMapper.toDto(account);
    }

}
