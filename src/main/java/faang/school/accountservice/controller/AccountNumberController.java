package faang.school.accountservice.controller;

import faang.school.accountservice.entity.AccountNumber;
import faang.school.accountservice.service.AccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accountNumbers")
public class AccountNumberController {

    private final AccountNumberService accountNumberService;

    @GetMapping()
    public Collection<String> getAccountNumbers(@RequestParam(required = false, defaultValue = "BANK") String prefix,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        return accountNumberService.getUniqueAccountNumber(prefix, size);
    }
}
