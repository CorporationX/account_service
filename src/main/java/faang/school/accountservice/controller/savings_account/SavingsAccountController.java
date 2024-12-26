package faang.school.accountservice.controller.savings_account;

import faang.school.accountservice.dto.savings_account.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponse;
import faang.school.accountservice.service.savings_account.SavingsAccountService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/savings-accounts")
@RequiredArgsConstructor
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountResponse createSavingsAccount(SavingsAccountCreateDto createDto) {
        return savingsAccountService.createSavingsAccount(createDto);
    }

    @GetMapping("/{savingsAccountId}")
    public SavingsAccountResponse getSavingsAccountById(@PathVariable @Min(1) long savingsAccountId) {
        return null;
    }
}
