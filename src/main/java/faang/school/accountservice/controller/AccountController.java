package faang.school.accountservice.controller;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserContext userContext;

    @GetMapping
    public List<AccountDto> getAllAccounts() {
        long id = userContext.getUserId();
        return accountService.getAllAccountsByOwnerId(id);
    }

    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable("id") Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@Valid @RequestBody AccountDto dto) {
        long ownerId = userContext.getUserId();
        return accountService.openAccount(ownerId, dto);
    }

    @PutMapping("/blocking/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockAccount(@PathVariable("id") Long id) {
        long ownerId = userContext.getUserId();
        accountService.blockAccount(ownerId, id);
    }

    @PutMapping("/closing/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAccount(@PathVariable("id") Long id) {
        long ownerId = userContext.getUserId();
        accountService.closeAccount(ownerId, id);
    }
}
