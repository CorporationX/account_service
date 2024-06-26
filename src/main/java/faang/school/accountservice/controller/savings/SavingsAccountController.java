package faang.school.accountservice.controller.savings;

import faang.school.accountservice.dto.savings.SavingsAccountDto;
import faang.school.accountservice.dto.savings.SavingsAccountToCreateDto;
import faang.school.accountservice.service.savings.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/savings")
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @PostMapping()
    public SavingsAccountDto openSavingsAccount(@RequestBody SavingsAccountToCreateDto dto) {
        return savingsAccountService.openSavingsAccount(dto);
    }

    @GetMapping("/{id}")
    public SavingsAccountDto getSavingsAccount(@PathVariable Long id) {
        return savingsAccountService.getSavingsAccount(id);
    }
}
