package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.saving.SavingAccountDto;
import faang.school.accountservice.dto.account.saving.SavingAccountFilter;
import faang.school.accountservice.dto.account.saving.SavingAccountCreateDto;
import faang.school.accountservice.service.account.SavingAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts/saving")
@RequiredArgsConstructor
public class SavingAccountController {
    private final SavingAccountService savingAccountService;

    @PostMapping
    public SavingAccountDto create(@Valid @RequestBody SavingAccountCreateDto requestDto) {
        return savingAccountService.openAccount(requestDto);
    }

    @GetMapping
    public List<SavingAccountDto> findBy(@ModelAttribute SavingAccountFilter filter) {
        return savingAccountService.findBy(filter);
    }

    @GetMapping("/{id}")
    public SavingAccountDto findById(@PathVariable Long id) {
        return savingAccountService.findById(id);
    }
}
