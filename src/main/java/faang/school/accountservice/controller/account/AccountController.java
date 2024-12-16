package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountRequestDto;
import faang.school.accountservice.dto.account.AccountResponseDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/new")
    public AccountResponseDto open(@RequestBody AccountRequestDto dto) {
        return accountService.open(dto);
    }

    @GetMapping("/{accountId}")
    public AccountResponseDto getAccount(@PathVariable @Positive long accountId) {
        return accountService.get(accountId);
    }

    @PostMapping("/filtered")
    public List<AccountResponseDto> getAccounts(@RequestBody AccountRequestDto dto) {
        return accountService.getAccountsByOwner(dto);
    }

    @PostMapping("/block")
    public AccountResponseDto blockAccount(@RequestBody AccountRequestDto dto) {
        return accountService.blockAccount(dto);
    }

    @PostMapping("/close")
    public AccountResponseDto closeAccount(@RequestBody AccountRequestDto dto) {
        return accountService.closeAccount(dto);
    }
}
