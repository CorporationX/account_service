package faang.school.accountservice.controller;

import faang.school.accountservice.dto.savingsAccount.BalanceDto;
import faang.school.accountservice.dto.savingsAccount.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savingsAccount.SavingsAccountResponseDto;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.service.SavingsAccountService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RequestMapping(value = "api/v1/savingsAccount")
@RestController
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;
    private final SavingsAccountMapper savingsAccountMapper;

    @PostMapping
    public SavingsAccountResponseDto openSavingsAccount(@RequestBody SavingsAccountRequestDto dto) {
        SavingsAccount savingsAccount = savingsAccountService.openSavingsAccount(dto);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    @GetMapping("/id")
    public SavingsAccountResponseDto getById(@RequestParam UUID id) {
        SavingsAccount savingsAccount = savingsAccountService.getById(id);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    @GetMapping("/accountId")
    public SavingsAccountResponseDto getById(@RequestParam Long accountId) {
        SavingsAccount savingsAccount = savingsAccountService.getByAccountId(accountId);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    @PostMapping("/deposit")
    public SavingsAccountResponseDto deposit(@RequestBody BalanceDto balanceDto){
        SavingsAccount savingsAccount = savingsAccountService.deposit(balanceDto);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    @PostMapping("/withdraw")
    public SavingsAccountResponseDto withdraw(@RequestBody BalanceDto balanceDto){
        SavingsAccount savingsAccount = savingsAccountService.withdraw(balanceDto);
        return savingsAccountMapper.toDto(savingsAccount);
    }
}
