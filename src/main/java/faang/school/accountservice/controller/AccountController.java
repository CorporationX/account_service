package faang.school.accountservice.controller;

import faang.school.accountservice.model.Account;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    public Account getAccount(@Positive @PathVariable("id") long id) {
        return accountService.getAccount(id);
    }
}
