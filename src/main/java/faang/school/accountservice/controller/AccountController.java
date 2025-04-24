package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.service.implementations.AccountServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@RequestHeader("x-user-id") long userId,
                                                      @PathVariable long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(@RequestHeader("x-user-id") long userId,
                                                       @Valid @RequestBody AccountRequest accountRequest) {
        AccountResponse response = accountService.createAccount(accountRequest);
        URI accountUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{responseId}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(accountUri).build();
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<AccountResponse> blockAccount(@RequestHeader("x-user-id") long userId,
                                                        @PathVariable long id) {
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<AccountResponse> closeAccount(@RequestHeader("x-user-id") long userId,
                                                        @PathVariable long id) {
        return ResponseEntity.ok(accountService.closeAccount(id));
    }
}
