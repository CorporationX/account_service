package faang.school.accountservice.controller;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/savings-account")
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;

    @GetMapping("/{savings-account-id}")
    public SavingsAccountDto getSavingsAccountById(@PathVariable Long id) {
        log.info("Received a request to retrieve savings account by id: {}", id);
        return savingsAccountService.getSavingsAccountById(id);
    }

    @PostMapping()
    public SavingsAccountDto createSavingsAccount(@RequestBody SavingsAccountDto savingsAccountDto) throws SQLException {
        log.info("Received a request to create savings account: {}", savingsAccountDto);
        return savingsAccountService.createAccount(savingsAccountDto);
    }
}
