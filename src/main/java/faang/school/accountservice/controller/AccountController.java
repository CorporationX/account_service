package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private static final String NUMBER_MORE_MSG = "Account number must be more or equals to 12 numbers";
    private static final String NUMBER_LESS_MSG = "Account number must be less or equals to 20 numbers";
    private static final String ONLY_NUMBERS_MSG = "Account number must contains only numbers";

    private final AccountService accountService;

    @GetMapping("/number/{number}")
    public AccountDto getAccountByNumber(@Size(min = 12, message = NUMBER_MORE_MSG)
                                         @Size(max = 20, message = NUMBER_LESS_MSG)
                                         @Pattern(regexp = "\\d+", message = ONLY_NUMBERS_MSG)
                                         @PathVariable String number) {
        return accountService.findByAccountNumber(number);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@RequestBody @Valid AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("/freezing/{number}")
    public AccountDto freezeAccount(@Size(min = 12, message = NUMBER_MORE_MSG)
                                    @Size(max = 20, message = NUMBER_LESS_MSG)
                                    @Pattern(regexp = "\\d+", message = ONLY_NUMBERS_MSG)
                                    @PathVariable String number) {
        return accountService.freezeAccount(number);
    }

    @DeleteMapping("/closure/{number}")
    public AccountDto closeAccount(@Size(min = 12, message = NUMBER_MORE_MSG)
                                   @Size(max = 20, message = NUMBER_LESS_MSG)
                                   @Pattern(regexp = "\\d+", message = ONLY_NUMBERS_MSG)
                                   @PathVariable String number) {
        return accountService.closeAccount(number);
    }
}
