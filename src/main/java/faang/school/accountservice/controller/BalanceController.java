package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.validator.CreateValidation;
import faang.school.accountservice.validator.UpdateValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/account/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BalanceDto getByAccountId(@PathVariable Long id) {
        return balanceService.getByAccountId(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BalanceDto getById(@PathVariable UUID id) {
        return balanceService.getById(id);
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceDto createBalance(@Validated(CreateValidation.class) @RequestBody BalanceDto balance) {
        return balanceService.create(balance);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BalanceDto updateBalance(@Validated(UpdateValidation.class) @RequestBody BalanceDto balance) {
        return balanceService.update(balance);
    }
}
