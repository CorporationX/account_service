package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/user/{userId}")
    public List<ResponseAccountDto> getAccountsByUserId(@PathVariable Long userId, Pageable pageable) {
        log.info("Getting all accounts for user with id: {}", userId);
        return accountService.getAccountsByUserId(userId, pageable);
    }

    @GetMapping("/project/{projectId}")
    public List<ResponseAccountDto> getAccountsByProjectId(@PathVariable Long projectId, Pageable pageable) {
        log.info("Getting all accounts for project with id: {}", projectId);
        return accountService.getAccountsByProjectId(projectId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAccountDto createAccount(@Valid @RequestBody CreateAccountDto createAccountDto) {
        log.info("Creating account for user {} or project {}", createAccountDto.userId(), createAccountDto.projectId());
        return accountService.createAccount(createAccountDto);
    }

    @PatchMapping("/{accountId}/block")
    public ResponseAccountDto blockAccount(@PathVariable UUID accountId) {
        log.info("Blocking account {}", accountId);
        return accountService.blockAccount(accountId);
    }

    @PatchMapping("/{accountId}/close")
    public ResponseAccountDto closeAccount(@PathVariable UUID accountId) {
        log.info("Closing account {}", accountId);
        return accountService.closeAccount(accountId);
    }
}
