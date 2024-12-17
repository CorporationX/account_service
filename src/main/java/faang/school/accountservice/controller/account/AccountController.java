package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/new")
    public AccountDtoResponse open(@RequestBody AccountDtoOpen dto) {
        return accountService.open(dto);
    }
}
