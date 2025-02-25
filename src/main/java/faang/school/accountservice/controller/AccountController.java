package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountReadDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{invoice}")
    public AccountReadDto getAccount(@PathVariable String invoice) {
        return accountService.getAccount(invoice);
    }

    @PostMapping
    public AccountReadDto openAccount(@RequestBody @Valid AccountCreateDto dto) {
        return accountService.openAccount(dto);
    }

    @PatchMapping("/{invoice}/freeze")
    public AccountReadDto freezeInvoice(@PathVariable String invoice) {
        return accountService.freezeInvoice(invoice);
    }

    @PatchMapping("/{invoice}/close")
    public AccountReadDto closeInvoice(@PathVariable String invoice) {
        return accountService.closeInvoice(invoice);
    }
}
