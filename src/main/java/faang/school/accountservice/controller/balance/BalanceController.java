package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.BalanceCreateRequest;
import faang.school.accountservice.dto.balance.BalanceUpdateRequest;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balances")
public class BalanceController {
    private final BalanceService balanceService;
    private final AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<BalanceDto> createBalance(@RequestBody BalanceCreateRequest balanceCreateRequest) {
        Account account = accountRepository.findById(balanceCreateRequest.getAccountId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found with id: " + balanceCreateRequest.getAccountId()));

        BalanceDto balanceDto = balanceService.createBalance(
                account,
                balanceCreateRequest.getAuthorisationBalance(),
                balanceCreateRequest.getActualBalance()
        );

        return ResponseEntity.ok(balanceDto);
    }

    @PutMapping
    public ResponseEntity<BalanceDto> updateBalance(@PathVariable Long balanceId,
                                                    @RequestBody BalanceUpdateRequest balanceUpdateRequest) {
        accountRepository.findById(balanceId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + balanceId));

        BalanceDto balanceDto = balanceService.updateBalance(
                balanceId,
                balanceUpdateRequest.getAuthorisationBalance(),
                balanceUpdateRequest.getActualBalance()
        );

        return ResponseEntity.ok(balanceDto);
    }

    @GetMapping
    public ResponseEntity<BalanceDto> getBalance(@PathVariable Long balanceId) {
        BalanceDto balanceDto = balanceService.getBalance(balanceId);

        return ResponseEntity.ok(balanceDto);
    }
}