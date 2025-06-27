package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountCreateProjectRequestDto;
import faang.school.accountservice.dto.account.AccountCreateUserRequestDto;
import faang.school.accountservice.dto.account.AccountResponseDto;
import faang.school.accountservice.facade.account.AccountFacade;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
public class AccountController {
    private AccountFacade accountFacade;
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable UUID accountId) {
        log.info("Account controller accepted request get account with id {}", accountId);

        AccountResponseDto response = accountFacade.getAccountById(accountId);
        log.info("Account controller return response get account {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/user")
    public ResponseEntity<AccountResponseDto> createAccountForUser
            (@RequestBody @Valid AccountCreateUserRequestDto accountCreateUserRequestDto) {
        log.info("Account controller accepted request create account for user {}", accountCreateUserRequestDto);

        AccountResponseDto response = accountFacade.createAccountForUser(accountCreateUserRequestDto);
        log.info("Account controller return response create account for user {}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/user")
    public ResponseEntity<AccountResponseDto> createAccountForProject
            (@RequestBody @Valid AccountCreateProjectRequestDto accountCreateProjectRequestDto) {
        log.info("Account controller accepted request create account for project {}", accountCreateProjectRequestDto);

        AccountResponseDto response = accountFacade.createAccountForProject(accountCreateProjectRequestDto);
        log.info("Account controller return response create account for project {}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
