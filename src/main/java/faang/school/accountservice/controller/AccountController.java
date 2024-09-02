package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.AccountUtilService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountUtilService accountUtilService;

    @GetMapping("/{id}")
    public AccountDto getAccount(@Positive @PathVariable Long id){
        return accountService.getAccount(id);
    }

    @GetMapping("/get_by_number/{number}")
    public AccountDto getAccountByNumber(@Size(min = 12, max = 20) @PathVariable String number){
        return accountService.getAccountByNumber(number);
    }

    @GetMapping("/get_by_owner/{ownerId}")
    public AccountDto getAccountByOwner(@Positive @PathVariable Long ownerId,
                                        @RequestParam("ownerType") String ownerType) {
        return accountService.getAccountByOwner(ownerId, ownerType);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@Valid @RequestBody OpenAccountDto openAccountDto){
        return accountService.openAccount(openAccountDto);
    }

    @PutMapping("/block/{id}")
    public AccountDto blockAccount(@Positive @PathVariable Long id){
        String status = Status.FROZEN.name();
        return accountUtilService.changeAccountStatus(id, status);
    }

    @PutMapping("/unblock/{id}")
    public AccountDto unblockAccount(@Positive @PathVariable Long id){
        String status = Status.ACTIVE.name();
        return accountUtilService.changeAccountStatus(id, status);
    }

    @PutMapping("/close/{id}")
    public AccountDto closeAccount(@Positive @PathVariable Long id){
        String status = Status.CLOSED.name();
        return accountUtilService.changeAccountStatus(id, status);
    }
}
