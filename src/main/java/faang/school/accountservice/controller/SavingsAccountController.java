package faang.school.accountservice.controller;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/savings-accounts")
@RequiredArgsConstructor
public class SavingsAccountController {
    private static final Logger logger = LoggerFactory.getLogger(SavingsAccountController.class);
    private final SavingsAccountService savingsAccountService;
    private final SavingsAccountMapper savingsAccountMapper;

    @PostMapping
    public SavingsAccountDto createSavingsAccount(Long accountId, Long tariffId) {
        logger.info("Создание счета для accountId: {}", accountId);
        SavingsAccount account = savingsAccountService.createSavingsAccount(accountId, tariffId);
        return savingsAccountMapper.toSavingsAccountResponseDto(account);
    }

    @GetMapping("/{id}")
    public SavingsAccountDto getSavingsAccountById(Long id) {
        logger.info("Запрос счета по ID: {}", id);
        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccountService.getSavingsAccountById(id));
    }

    @GetMapping("/by-account/{accountId}")
    public SavingsAccountDto getSavingsAccountByAccountId(Long accountId) {
        logger.info("Запрос счета для accountId: {}", accountId);
        return savingsAccountMapper.toSavingsAccountResponseDto(savingsAccountService.getSavingsAccountByAccountId(accountId));
    }
}