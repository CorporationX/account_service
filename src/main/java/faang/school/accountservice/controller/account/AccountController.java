package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.service.free_account.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/accounts")
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final FreeAccountNumbersService service;

    @GetMapping()
    public AccountDto getAccount(AccountType type) {
        return service.getNewAccount(type);
    }

    @PostMapping()
    public void generateNewAccount(AccountType type, Currency currency) {
        service.generateAccountNumber(type, currency);
    }
}
