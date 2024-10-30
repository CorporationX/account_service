package faang.school.accountservice.controller;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.service.SavingsAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/savings_account")
@RequiredArgsConstructor
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto openSavingsAccount(@RequestBody @Valid @NotNull SavingsAccountDto savingsAccountDto) {
        return savingsAccountService.openSavingsAccount(savingsAccountDto);
    }

    @GetMapping("/{id}")
    public SavingsAccountDto getSavingsAccountById(@PathVariable @Positive Long id) {
        return savingsAccountService.getSavingsAccountById(id);
    }

    @GetMapping("/{userId}")
    public SavingsAccountDto getSavingsAccountByUserId(@PathVariable @Positive Long userId) {
        return savingsAccountService.getSavingsAccountByUserId(userId);
    }
}
