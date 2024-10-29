package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.service.impl.SavingsAccountServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/savings-account")
@RequiredArgsConstructor
@Validated
public class SavingsAccountController {
    private final SavingsAccountServiceImpl savingsAccountService;

    @Operation(summary = "Create SavingsAccount", description = "Create SavingsAccount in DB")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public SavingsAccountDto openSavingAccount(@RequestBody SavingsAccountDto savingsAccountDto) {
        return savingsAccountService.openSavingsAccount(savingsAccountDto);
    }

    @Operation(summary = "Get SavingsAccount by id", description = "Get SavingsAccount from DB by id")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public SavingsAccountDto getSavingAccount(@NotNull @Positive(message = "id must be bigger than 0") @PathVariable() Long id) {
        return savingsAccountService.getSavingsAccount(id);
    }

    @Operation(summary = "Get SavingsAccount by user id", description = "Get SavingsAccount from DB by user id")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public SavingsAccountDto getSavingAccountByUserId(@NotNull @Positive(message = "id must be bigger than 0") @RequestParam Long userId) {
        return savingsAccountService.getSavingsAccount(userId);
    }



}
