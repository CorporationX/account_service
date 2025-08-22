package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;

    @PostMapping
    public AccountDto create(@RequestBody @Valid CreateAccountDto accountDto) {
        return service.create(accountDto);
    }

    @GetMapping("/id/{id}")
    public AccountDto getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/number/{number}")
    public AccountDto getByNumber(@PathVariable String number) {
        return service.findByNumber(number);
    }

    @GetMapping("/users/{id}")
    public List<AccountDto> getAllByUserId(@PathVariable long id) {
        return service.findByUserId(id);
    }

    @GetMapping("/projects/{id}")
    public List<AccountDto> getAllByProjectId(@PathVariable long id) {
        return service.findByProjectId(id);
    }

    @PutMapping("/{id}/block")
    public void block(@PathVariable UUID id) {
        service.block(id);
    }

    @PutMapping("/{id}/close")
    public void close(@PathVariable UUID id) {
        service.close(id);
    }
}
