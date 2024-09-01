package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService service;

    @GetMapping("/{number}")
    public AccountDto getAccount(@PathVariable String number) {
        return service.getAccount(number);
    }

    @PostMapping
    public AccountDto openAccount(@RequestBody AccountDto dto) {
        return service.openAccount(dto);
    }

    @PutMapping("/{number}")
    public void updateAccountStatus(@PathVariable String number,
                                    @RequestParam(name = "status") String status) {
        service.updateAccountStatus(number, status);
    }
}