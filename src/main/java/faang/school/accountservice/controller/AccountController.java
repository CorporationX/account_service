package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<ResponseAccountDto> getAccounts(@RequestParam(required = false) Long userId,
                                                @RequestParam(required = false) Long projectId) {
        return accountService.getAccounts(userId, projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAccountDto createAccount(@Valid @RequestBody CreateAccountDto createAccountDto) {
        return accountService.createAccount(createAccountDto);
    }

    @PatchMapping("/{accountId}/status")
    public ResponseAccountDto updateAccountStatus(@PathVariable UUID accountId,
                                                  @RequestBody UpdateAccountDto updateDto) {
        return accountService.updateAccountStatus(accountId, updateDto.status());
    }
}
