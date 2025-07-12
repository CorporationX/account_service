package faang.school.accountservice.controller;

import faang.school.accountservice.model.AccountBalanceType;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final FreeAccountNumbersService freeAccountNumbersService;

    @PostMapping("create")
    public void oneAccNumberPerType() {
        freeAccountNumbersService.createOneFreeAccNumberPerType(AccountBalanceType.DEBIT);
    }

    @GetMapping("get")
    public FreeAccountNumber getFreeAccNumberByType() {
        return freeAccountNumbersService.getFreeAccNumberByType(AccountBalanceType.DEBIT);
    }

    @PostMapping("create/batch")
    public void createQuantityOfNewAccNumbers() {
        freeAccountNumbersService.createQuantityOfNewAccNumbers(AccountBalanceType.DEBIT, 400);
    }

    @PostMapping("create/target")
    public void createTargetQuantityOfAccNumbers (){
        freeAccountNumbersService.createTargetQuantityOfAccNumbers(AccountBalanceType.DEBIT, 1000);
    }
}
