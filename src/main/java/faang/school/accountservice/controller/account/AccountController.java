package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.RequestAccountDto;
import faang.school.accountservice.dto.ResponseAccountDto;
import faang.school.accountservice.service.account.AccountAction;
import faang.school.accountservice.service.account.impl.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/v1/accounts")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<ResponseAccountDto> get(@PathVariable @Positive long accountId){
        return ResponseEntity.ok().body(accountService.get(accountId));
    }

    @PostMapping
    public ResponseEntity<Void> open(@RequestBody @Valid RequestAccountDto accountDto){
        accountService.open(accountDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{accountId}/block")
    public ResponseEntity<Void> block(@PathVariable @Positive long accountId){
        accountService.applyAccountAction(accountId, AccountAction.BLOCKED);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{accountId}/close")
    public ResponseEntity<Void> close(@PathVariable @Positive long  accountId){
        accountService.applyAccountAction(accountId, AccountAction.CLOSED);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{accountId}/unblock")
    public ResponseEntity<Void> unblock(@PathVariable @Valid long  accountId){
        accountService.applyAccountAction(accountId, AccountAction.UNBLOCKED);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}