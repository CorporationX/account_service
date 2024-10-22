package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.service.impl.AccountServiceImpl;
import faang.school.accountservice.validator.AccountControllerValidator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private final AccountControllerValidator validator;
    private final AccountServiceImpl accountService;

    @Operation(summary = "Get account", description = "Get account from DB by id")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @Operation(summary = "Create account", description = "Create account in DB")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public AccountDto openAccount(@RequestBody AccountDto accountDto) {
        validator.checkDto(accountDto);

        return accountService.openAccount(accountDto);
    }

    @Operation(summary = "Block account", description = "Block account by Id")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public AccountDto blockAccount(@PathVariable Long id) {
        return accountService.blockAccount(id);
    }

    @Operation(summary = "Block account by account number", description = "Block account by account number")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/number/{number}")
    public AccountDto blockAccountNumber(@PathVariable String number) {
        return accountService.blockAcccountNumber(number);
    }

}
