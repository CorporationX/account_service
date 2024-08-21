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

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.ACCOUNT)
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{ownerId}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable("ownerId") long ownerId) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccount(ownerId));
    }

    @PostMapping
    public ResponseEntity<AccountDto> openAccount(@Valid @RequestBody AccountDto accountDto) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.openAccount(accountDto));
    }
    @PutMapping
    public ResponseEntity<AccountDto> updateAccount(@PathVariable("ownerId") long ownerId,
                                                   @NotBlank @RequestParam("status") String status) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccount(ownerId, status));
    }
}
