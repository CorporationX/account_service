package faang.school.accountservice.controller;

import faang.school.accountservice.dto.OpenSavingsAccountRequest;
import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/savings-accounts")
@RequiredArgsConstructor
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;

    @PostMapping
    public SavingsAccountDto openSavingsAccount(@RequestBody OpenSavingsAccountRequest request){
        return savingsAccountService.openSavingsAccount(request);
    }

    @GetMapping("/{id}")
    public SavingsAccountDto getSavingsAccountById(@PathVariable Long id){
        return savingsAccountService.getSavingsAccountById(id);
    }

    @GetMapping("/owner/{ownerId}")
    public SavingsAccountDto getSavingsAccountByOwnerId(@PathVariable Long ownerId) {
        return savingsAccountService.getSavingsAccountByOwnerId(ownerId);
    }
}
