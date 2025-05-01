package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountOperationResponse;
import faang.school.accountservice.service.AccountOperationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountOperationController {
    private final AccountOperationService accountOperationService;

    @PostMapping
    public ResponseEntity<AccountOperationResponse> getOperation(@RequestBody @NotNull UUID id) {
        AccountOperationResponse response = accountOperationService.getOperation(id);

        return ResponseEntity.ok(response);
    }
}
