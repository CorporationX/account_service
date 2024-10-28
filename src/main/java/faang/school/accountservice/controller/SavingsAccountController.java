package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.SavingsAccountDto;
import faang.school.accountservice.service.impl.SavingsAccountServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/savings-account")
@RequiredArgsConstructor
public class SavingsAccountController {
    private final SavingsAccountServiceImpl savingsAccountService;

    @Operation(summary = "Create SavingsAccount", description = "Create SavingsAccount in DB")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public SavingsAccountDto openSavingAccount(@RequestBody SavingsAccountDto savingsAccountDto) {
        return savingsAccountService.openSavingsAccount(savingsAccountDto);
    }

    @Operation(summary = "Get SavingsAccount", description = "Get SavingsAccount from DB")
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/{id}")
    public SavingsAccountDto getSavingAccount(@PathVariable() Long id) {
        return savingsAccountService.getSavingsAccount(id);
    }

}
