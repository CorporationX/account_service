package faang.school.accountservice.controller;

import faang.school.accountservice.entity.AccountNumber;
import faang.school.accountservice.service.AccountNumberService;
import faang.school.accountservice.validator.ValidAccountNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

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

    @PostMapping("/generate")
    public AccountNumber generateAccountNumber(@RequestBody @Valid GenerateAccountNumberRequest request) {

        return null;
    }

    @Data
    @AllArgsConstructor
    @ValidAccountNumber
    public static class GenerateAccountNumberRequest {
        @NotBlank
        private String prefix;
        @Positive
        private Integer size;
        @Positive
        private Integer accountLength;
    }
}
