package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffAndRateDto;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/savingsaccount")
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;

    @GetMapping("/{id}")
    public TariffAndRateDto getAccountByAccountId(@PathVariable("id") Long accountId){
        return savingsAccountService.getTariffAndRateByAccountId(accountId);
    }

    @GetMapping
    public TariffAndRateDto getAccountByClientId(@RequestParam("id") String ownerId){
        return savingsAccountService.getAccountByClientId(ownerId);
    }

    @PostMapping
    public TariffAndRateDto openAccount(@RequestBody SavingsAccountDto accountDto){
        return savingsAccountService.openAccount(accountDto);
    }
}

