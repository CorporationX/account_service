package faang.school.accountservice.controller;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.OpenAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccessDeniedException;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;
    private final UserContext userContext;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> get(@PathVariable Long id) {
        AccountResponseDto account = accountService.get(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<AccountResponseDto> getByNumber(@PathVariable String number) {
        AccountResponseDto account = accountService.getByNumber(number);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<AccountResponseDto>> getByOwner(
            @PathVariable Long ownerId,
            @RequestParam OwnerType ownerType,
            @PageableDefault(size = 20) Pageable pageable) {

        // Проверка доступа: ADMIN или владелец
        boolean isAdmin = userContext.hasRole("ADMIN");
        Long currentUserId = userContext.getUserId();
        boolean isOwner = currentUserId != null && ownerId.equals(currentUserId);
        
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied. Only ADMIN role or account owner can access this resource.");
        }

        Page<AccountResponseDto> accounts = accountService.getByOwner(ownerId, ownerType, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/owner/{ownerId}/active")
    public ResponseEntity<Page<AccountResponseDto>> getActiveByOwner(
            @PathVariable Long ownerId,
            @RequestParam OwnerType ownerType,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AccountResponseDto> accounts = accountService.getActiveByOwner(ownerId, ownerType, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/owner/{ownerId}/active/currency/{currency}")
    public ResponseEntity<Page<AccountResponseDto>> getActiveByOwnerAndCurrency(
            @PathVariable Long ownerId,
            @RequestParam OwnerType ownerType,
            @PathVariable Currency currency,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AccountResponseDto> accounts = accountService.getActiveByOwnerAndCurrency(ownerId, ownerType, currency, pageable);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> open(@Valid @RequestBody OpenAccountDto dto) {
        AccountResponseDto account = accountService.open(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<AccountResponseDto> block(@PathVariable Long id) {
        AccountResponseDto account = accountService.block(id);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<AccountResponseDto> unblock(@PathVariable Long id) {
        AccountResponseDto account = accountService.unblock(id);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<AccountResponseDto> close(@PathVariable Long id) {
        AccountResponseDto account = accountService.close(id);
        return ResponseEntity.ok(account);
    }
}

