package faang.school.accountservice.controller.balance;

import faang.school.accountservice.controller.facade.BalanceFacade;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceFacade balanceFacade;

    @GetMapping("/{accountId}")
    public ResponseEntity<BalanceDto> get(@PathVariable Long accountId) {
        return ResponseEntity.ok(balanceFacade.getBalance(accountId));
    }

    @PostMapping("/create/{accountId}")
    public ResponseEntity<BalanceDto> create(@PathVariable UUID accountId) {
        return ResponseEntity.ok(balanceFacade.createBalance(accountId));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<BalanceDto> update(
            @PathVariable Long accountId,
            @RequestBody UpdateBalanceDto request
    ) {
        return ResponseEntity.ok(balanceFacade.updateBalance(accountId, request));
    }
}
