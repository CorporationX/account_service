package faang.school.accountservice.controller;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("test/")
@RequiredArgsConstructor
public class TestController {
    private final FreeAccountNumbersService service;


    @GetMapping
    public void getNumber() {
        service.createNewAccountNumber(AccountType.CREDIT_ACCOUNT);
        service.consumeFreeNumber(AccountType.CREDIT_ACCOUNT, log::info);
    }
}
