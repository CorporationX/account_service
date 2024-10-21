package faang.school.accountservice.controller;

import faang.school.accountservice.dto.savings_account.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savings_account.UpdateTariffDto;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.service.SavingsAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/savings-accounts")
@RestController
public class SavingsAccountController {
    private final AccountMapper accountMapper;
    private final SavingsAccountMapper mapper;
    private final SavingsAccountService service;

    @PostMapping
    public SavingsAccountResponseDto create(@RequestBody @Valid SavingsAccountCreateDto dto) {
        Account account = accountMapper.toEntity(dto.getAccountDto());
        SavingsAccount savingsAccount = mapper.toEntity(dto);
        SavingsAccount createdSavingsAccount = service.create(account, savingsAccount, dto.getTariffType());
        return mapper.toResponseDto(createdSavingsAccount);
    }

    @GetMapping("/{id}")
    public SavingsAccountResponseDto getById(@PathVariable Long id) {
        SavingsAccount foundSavingsAccount = service.findById(id);
        return mapper.toResponseDto(foundSavingsAccount);
    }

    @PatchMapping
    public SavingsAccountResponseDto updateTariff(@RequestBody @Valid UpdateTariffDto dto) {
        SavingsAccount updatedSavingsAccount = service.updateTariff(dto.getSavingsAccountId(), dto.getNewTariffType());
        return mapper.toResponseDto(updatedSavingsAccount);
    }
}
