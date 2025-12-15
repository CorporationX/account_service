package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.OpenAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @GetMapping("/{id}")
    public AccountResponseDto get(@PathVariable @NotNull Long id) {
        return accountService.get(id);
    }

    @GetMapping("/number/{number}")
    public AccountResponseDto getByNumber(@PathVariable @NotBlank String number) {
        return accountService.getByNumber(number);
    }

    @GetMapping("/owner/{ownerId}")
    public Page<AccountResponseDto> getByOwner(
            @PathVariable @NotNull Long ownerId,
            @RequestParam @NotNull OwnerType ownerType,
            @PageableDefault(size = 20) @NotNull Pageable pageable) {
        return accountService.getByOwner(ownerId, ownerType, pageable);
    }

    @GetMapping("/owner/{ownerId}/active")
    public Page<AccountResponseDto> getActiveByOwner(
            @PathVariable @NotNull Long ownerId,
            @RequestParam @NotNull OwnerType ownerType,
            @PageableDefault(size = 20) @NotNull Pageable pageable) {
        return accountService.getActiveByOwner(ownerId, ownerType, pageable);
    }

    @GetMapping("/owner/{ownerId}/active/currency/{currency}")
    public Page<AccountResponseDto> getActiveByOwnerAndCurrency(
            @PathVariable @NotNull Long ownerId,
            @RequestParam @NotNull OwnerType ownerType,
            @PathVariable @NotNull Currency currency,
            @PageableDefault(size = 20) @NotNull Pageable pageable) {
        return accountService.getActiveByOwnerAndCurrency(ownerId, ownerType, currency, pageable);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> open(@Valid @RequestBody OpenAccountDto dto) {
        AccountResponseDto account = accountService.open(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PatchMapping("/{id}/block")
    public AccountResponseDto block(@PathVariable @NotNull Long id) {
        return accountService.block(id);
    }

    @PatchMapping("/{id}/unblock")
    public AccountResponseDto unblock(@PathVariable @NotNull Long id) {
        return accountService.unblock(id);
    }

    @PatchMapping("/{id}/close")
    public AccountResponseDto close(@PathVariable @NotNull Long id) {
        return accountService.close(id);
    }
}

