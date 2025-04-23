package faang.school.accountservice.controller;

import faang.school.accountservice.service.TariffAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assignment-tariffs")
public class TariffAssignmentController {

    private final TariffAssignmentService tariffAssignmentService;

    @PatchMapping("/{tariffId}/account/{accountId}")
    public ResponseEntity<String> assignTariffOnSavingsAccount(@PathVariable Long accountId, @PathVariable Long tariffId) {
        tariffAssignmentService.assignTariffOnSavingsAccount(accountId, tariffId);
        return ResponseEntity.ok("Tariff assigned on savings account successfully");
    }
}
