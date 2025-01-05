package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountReq;
import faang.school.accountservice.dto.account.AccountResp;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.utilities.UrlUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.ACCOUNTS)
public class AccountController {
    private final AccountService accountService;

    @GetMapping(UrlUtils.ID)
    public AccountResp getAccount(@PathVariable @Min(1) Long id) {
        return accountService.getAccount(id);
    }

    @PostMapping()
    public void openAccount(@Valid @RequestBody AccountReq accountReq, @RequestParam boolean isProjectAccount) {
        accountService.openAccount(accountReq, isProjectAccount);
    }

    @PatchMapping(UrlUtils.ID + UrlUtils.BLOCK)
    public void blockAccount(@PathVariable @Min(1) Long id) {
        accountService.blockAccount(id);
    }

    @PatchMapping(UrlUtils.ID + UrlUtils.CLOSE)
    public void closeAccount(@PathVariable @Min(1) Long id) {
        accountService.closeAccount(id);
    }
}
