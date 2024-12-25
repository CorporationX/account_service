package faang.school.accountservice.controller;


import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final FreeAccountNumberService freeAccountNumberService;

    @PostMapping("/process/{type}")
    public String processAccountNumber(@PathVariable("type") AccountType type) {
        return freeAccountNumberService.processAccountNumber(type);
    }
}