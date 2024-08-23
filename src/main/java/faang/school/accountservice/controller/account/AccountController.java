package faang.school.accountservice.controller.account;

import faang.school.accountservice.controller.ApiPath;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.ACCOUNT)
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{ownerId}")
    public ResponseEntity<List<AccountDto>> getAllAccounts(@PathVariable("ownerId") long ownerId,
                                                           @RequestParam("owner") String owner) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAllAccounts(ownerId, owner));
    }

    @GetMapping
    public ResponseEntity<AccountDto> getAccount(@RequestParam("number") String number) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccount(number));
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountDto accountDto) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.createAccount(accountDto));
    }
    @PutMapping
    public ResponseEntity<AccountDto> updateStatusAccount(@RequestParam("number") String number,
                                                   @NotBlank @RequestParam("status") String status) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.updateStatusAccount(number, status));
    }
}
