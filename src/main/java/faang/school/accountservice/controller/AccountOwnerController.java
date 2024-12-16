package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountOwnerRequest;
import faang.school.accountservice.dto.AccountOwnerResponse;
import faang.school.accountservice.dto.AccountOwnerWithAccountsResponse;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.service.AccountOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class AccountOwnerController {

    private final AccountOwnerService accountOwnerService;

    @PostMapping
    public ResponseEntity<AccountOwnerResponse> createOwner(@RequestBody AccountOwnerRequest request) {
        return ResponseEntity.ok(accountOwnerService.createOwner(request));
    }

    @GetMapping("/search")
    public ResponseEntity<AccountOwnerWithAccountsResponse> getOwnerWithAccountsByOwnerIdAndType(
            @RequestParam Long ownerId,
            @RequestParam OwnerType ownerType) {
        return ResponseEntity.ok(accountOwnerService.getOwnerWithAccountsByOwnerIdAndType(ownerId, ownerType));
    }
}
