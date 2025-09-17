package faang.school.accountservice.controller;

import faang.school.accountservice.service.AccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accountNumbers")
@Validated()
public class AccountNumberController {

    private final AccountNumberService accountNumberService;

    @GetMapping()
    public Collection<String> getAccountNumbers(@RequestParam String prefix,
                                                @RequestParam Integer size) {
        return accountNumberService.getUniqueAccountNumber(prefix, size);
    }
}
