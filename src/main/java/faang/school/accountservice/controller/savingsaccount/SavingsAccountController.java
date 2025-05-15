package faang.school.accountservice.controller.savingsaccount;

import faang.school.accountservice.dto.savingsaccount.DepositDto;
import faang.school.accountservice.dto.savingsaccount.OpenSavingsAccountDto;
import faang.school.accountservice.dto.savingsaccount.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savingsaccount.WithdrawDto;
import faang.school.accountservice.service.savingsaccount.SavingsAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/savings-accounts")
@RequiredArgsConstructor
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;

    @PostMapping()
    public ResponseEntity<SavingsAccountResponseDto> openSavingsAccount(
            @RequestBody @Valid OpenSavingsAccountDto openSavingsAccountDto
    ) {
        SavingsAccountResponseDto response = savingsAccountService
                .openSavingsAccount(openSavingsAccountDto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<SavingsAccountResponseDto> getByAccountId(
            @PathVariable String accountId
    ) {
        SavingsAccountResponseDto response = savingsAccountService
                .getSavingsAccountById(accountId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{savingsAccountId}/tariff")
    public ResponseEntity<SavingsAccountResponseDto> changeTariff(
            @PathVariable Long savingsAccountId,
            @RequestParam Long newTariffId
    ) {
        SavingsAccountResponseDto response = savingsAccountService
                .changeTariff(savingsAccountId, newTariffId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<SavingsAccountResponseDto> deposit(
            @RequestBody @Valid DepositDto depositDto
    ) {
        SavingsAccountResponseDto response = savingsAccountService
                .deposit(depositDto);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<SavingsAccountResponseDto> withdraw(
            @RequestBody @Valid WithdrawDto withdrawDto
    ) {
        SavingsAccountResponseDto response = savingsAccountService
                .withdraw(withdrawDto);
        return ResponseEntity.ok().body(response);
    }
}
