package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.service.account.SavingsAccountService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/savings-account")
@RequiredArgsConstructor
@Validated
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public SavingsAccountDto openSavingAccount(@RequestBody @Validated(SavingsAccountDto.Create.class) SavingsAccountDto savingsAccountDto) {
        return savingsAccountService.openSavingsAccount(savingsAccountDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public SavingsAccountDto getSavingAccount(@NotNull @Positive(message = "id must be positive") @PathVariable() Long id) {
        return savingsAccountService.getSavingsAccount(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public List<SavingsAccountDto> getSavingAccountByUserId(@NotNull @Positive(message = "id must be positive") @RequestParam Long userId) {
        return savingsAccountService.getSavingsAccountByUserId(userId);
    }


}