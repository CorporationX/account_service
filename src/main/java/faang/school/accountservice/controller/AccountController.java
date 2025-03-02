package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@RequestBody AccountCreateDto accountCreateDto) {
        return accountService.openAccount(accountCreateDto);
    }

    @GetMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> getAccountByProjectId(@PathVariable long projectId) {
        return accountService.getAccountByProjectId(projectId);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> getAccountByUserId(@PathVariable long userId) {
        return accountService.getAccountByUserId(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getAccountDtoById(@PathVariable long id) {
        return accountService.getAccountDtoById(id);
    }

    @PutMapping("/frieze/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto friezeAccount(@PathVariable long id) {
        return accountService.friezeAccount(id);
    }

    @PutMapping("/close/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto closeAccount(@PathVariable long id) {
        return accountService.closeAccount(id);
    }
}