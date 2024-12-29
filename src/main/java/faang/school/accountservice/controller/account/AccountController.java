package faang.school.accountservice.controller.account;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.account.RequestAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.account.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final UserContext userContext;

    @GetMapping()
    public ResponseAccountDto getFreeAccount(@RequestBody RequestAccountDto requestAccountDto) {
        return freeAccountNumbersService.getFreeAccount(requestAccountDto, userContext.getUserId());
    }

    @GetMapping("/{accountId}")
    public ResponseAccountDto getAccountWithId(@PathVariable long accountId) {
        return accountService.getAccountWithId(accountId, userContext.getUserId());
    }

    @GetMapping("/number/{number}")
    public ResponseAccountDto getAccountWithNumber(@PathVariable String number) {
        return accountService.getAccountWithNumber(number, userContext.getUserId());
    }

    @PutMapping("/{accountId}/block")
    public ResponseAccountDto blockAccount(@PathVariable long accountId) {
        return accountService.blockAccount(accountId, userContext.getUserId());
    }
    @PutMapping("/{accountId}/unblock")
    public ResponseAccountDto unblockAccount(@PathVariable long accountId) {
        return accountService.unblockAccount(accountId, userContext.getUserId());
    }
    @PutMapping("/{accountId}/close")
    public ResponseAccountDto closeAccount(@PathVariable long accountId) {
        return accountService.closeAccount(accountId, userContext.getUserId());
    }

}
