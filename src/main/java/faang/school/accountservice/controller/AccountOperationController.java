package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountOperationResponse;
import faang.school.accountservice.service.account.AccountOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Account Operations", description = "API for managing account operations")
public class AccountOperationController {
    private final AccountOperationService accountOperationService;

    @Operation(
            summary = "Get operation details",
            description = "Retrieves detailed information about an account operation by its ID"
    )
    @GetMapping
    public ResponseEntity<AccountOperationResponse> getOperation(@RequestBody @NotNull UUID id) {
        AccountOperationResponse response = accountOperationService.getOperation(id);

        return ResponseEntity.ok(response);
    }
}
