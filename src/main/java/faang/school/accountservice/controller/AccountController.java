package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountPreviewDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.OwnerRequest;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public AccountDto openAccount(@RequestBody @NotNull @Valid CreateAccountDto createAccountDto) {
        return accountService.openAccount(createAccountDto);
    }

    @PatchMapping("/{id}/freeze")
    public void freezeAccount(@PathVariable @NotNull @Positive Long id) {
        accountService.freezeAccount(id);
    }

    @PatchMapping("/{id}/unfreeze")
    public void unfreezeAccount(@PathVariable @NotNull @Positive Long id) {
        accountService.unfreezeAccount(id);
    }

    @PatchMapping("/{id}/close")
    public void closeAccount(@PathVariable @NotNull @Positive Long id) {
        accountService.closeAccount(id);
    }

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable @NotNull @Positive Long id) {
        return accountService.getAccountById(id);
    }

    @GetMapping("/user")
    public List<AccountPreviewDto> getAccountsByOwner(@RequestBody @NotNull @Valid OwnerRequest ownerRequest,
                                                      Pageable pageable) {
        return accountService.findAccountsByOwner(ownerRequest, pageable);
    }
}
