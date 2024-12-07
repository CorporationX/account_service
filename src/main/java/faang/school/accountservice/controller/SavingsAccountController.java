package faang.school.accountservice.controller;

import faang.school.accountservice.dto.savings_account.OpenSavingsAccountDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savings_account.UpdateTariffDto;
import faang.school.accountservice.entity.savings_account.SavingsAccount;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.service.savings_account.SavingsAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/savings-accounts")
@RestController
public class SavingsAccountController {
    private final SavingsAccountMapper savingsAccountMapper;
    private final SavingsAccountService savingsAccountService;

    @PostMapping
    public SavingsAccountResponseDto openSavingsAccount(@RequestBody @Valid OpenSavingsAccountDto dto) {
        SavingsAccount savingsAccount = savingsAccountMapper.toEntity(dto);

        UUID accountId = dto.getAccountId();
        Long tariffId = dto.getTariffId();
        BigDecimal amount = dto.getAmount();
        SavingsAccount savedSavingsAccount = savingsAccountService.openSavingsAccount(savingsAccount, accountId, tariffId, amount);

        return savingsAccountMapper.toResponseDto(savedSavingsAccount);
    }

    @GetMapping("/savings/{id}")
    public SavingsAccountResponseDto getSavingsAccountById(@PathVariable UUID id) {
        SavingsAccount savingsAccount = savingsAccountService.getById(id);
        return savingsAccountMapper.toResponseDto(savingsAccount);
    }

    @PatchMapping
    public SavingsAccountResponseDto updateTariff(@RequestBody @Valid UpdateTariffDto updateTariffDto) {
        UUID savingsAccountId = updateTariffDto.getSavingsAccountId();
        Long tariffId = updateTariffDto.getTariffId();
        SavingsAccount savingsAccount = savingsAccountService.updateTariff(savingsAccountId, tariffId);

        return savingsAccountMapper.toResponseDto(savingsAccount);
    }

    @GetMapping("/check/{id}")
    public void getSavingsAccountById(@PathVariable Long id) {
        savingsAccountService.updateBonusAfterAchievementAccepted(id, 1L);
    }
}
