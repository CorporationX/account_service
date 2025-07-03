package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceRequestDto;
import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.facade.balance.BalanceFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/balances")
@Slf4j
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceFacade balanceFacade;
    @GetMapping("/{balanceId}")
    public ResponseEntity<BalanceResponseDto> getBalanceById(@PathVariable UUID balanceId) {
        log.info("Balance controller accepted request get balance with id {}", balanceId);

        BalanceResponseDto response = balanceFacade.getBalanceById(balanceId);
        log.info("Account controller return response get account {}", response);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/authorize")
    public ResponseEntity<BalanceResponseDto> authorizeBalance
            (@RequestBody @Valid BalanceRequestDto balanceRequestDto) {
        log.info("Balance controller accepted request authorize balance {}", balanceRequestDto);

        BalanceResponseDto response = balanceFacade.authorizeBalance(balanceRequestDto);
        log.info("Balance controller return response authorize balance {}", response);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/clear")
    public ResponseEntity<BalanceResponseDto> clearAuthorizationBalance
            (@RequestBody @Valid BalanceRequestDto balanceRequestDto) {
        log.info("Balance controller accepted request clear authorization balance {}", balanceRequestDto);

        BalanceResponseDto response = balanceFacade.clearAuthorizationBalance(balanceRequestDto);
        log.info("Balance controller return response clear authorization balance {}", response);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cancel")
    public ResponseEntity<BalanceResponseDto> cancelAuthorizationBalance
            (@RequestBody @Valid BalanceRequestDto balanceRequestDto) {
        log.info("Balance controller accepted request cancel authorization balance {}", balanceRequestDto);

        BalanceResponseDto response = balanceFacade.cancelAuthorizationBalance(balanceRequestDto);
        log.info("Balance controller return response cancel authorization balance {}", response);

        return ResponseEntity.ok(response);
    }
}
