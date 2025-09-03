package faang.school.accountservice.controller;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts/savings")
@Validated
@RequiredArgsConstructor
public class SavingsAccountController {
    private final SavingsAccountService service;

    @PostMapping("{accountId}/tariffs/{tariffId}")
    public SavingsAccountDto create(@PathVariable UUID accountId,
                                    @PathVariable long tariffId) {
        return service.create(accountId, tariffId);
    }

    @GetMapping("/{id}")
    public SavingsAccountDto findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/users/{id}")
    public SavingsAccountDto findByUserId(@PathVariable Long id) {
        return service.findByUserId(id);
    }

    @GetMapping("/projects/{id}")
    public SavingsAccountDto findByProjectId(@PathVariable Long id) {
        return service.findByProjectId(id);
    }
}
