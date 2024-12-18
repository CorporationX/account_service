package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.dto.account.AccountDtoVerify;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/open")
    public AccountDtoResponse open(@RequestBody @Valid AccountDtoOpen dto) {
        return accountService.open(dto);
    }

    @PostMapping("/verify")
    public AccountDtoResponse verify(@RequestBody @Valid AccountDtoVerify dto) {
        return accountService.verify(dto);
    }

    @GetMapping("/{id}")
    public AccountDtoResponse getAccount(@PathVariable("id") @NotNull @Positive Long id) {
        return accountService.getAccount(id);
    }

    @PostMapping("/")
    public List<AccountDtoResponse> getAccounts(@RequestBody @Valid AccountDtoFilter dto) {
        return accountService.getAccounts(dto);
    }

}
