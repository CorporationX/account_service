package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountUpdateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AccountController — описание класса.
 * <p>
 * Предоставляет эндпоинты для открытия, получения счета, а так же его блокировки
 * и закрытия
 * </p>
 *
 * @author mrnght
 * @since 22.08.2025
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService service;

    @PostMapping
    public ResponseEntity<AccountViewDto> createAccount(@RequestBody AccountCreateDto createDto) {
        return ResponseEntity.ok(service.openAccount(createDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountViewDto> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAccount(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountViewDto> changeAccountStatus(@PathVariable Long id,
                                                       @RequestBody AccountUpdateDto updateDto) {
        return ResponseEntity.ok(service.changeAccountStatus(id, updateDto));
    }
}
