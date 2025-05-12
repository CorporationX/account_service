package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balances")
@Validated
public class BalanceController {
    private static final String INVALID_ID_MSG = "should be more than 1";
    private final BalanceService balanceService;

    @GetMapping("/{balanceId}")
    public ResponseEntity<ResponseBalanceDto> find(
            @PathVariable @Min(value = 1, message = INVALID_ID_MSG) long balanceId) {
        return ResponseEntity.ok().body(balanceService.find(balanceId));
    }

    @PutMapping
    public ResponseEntity<ResponseBalanceDto> update(@RequestBody @Valid UpdateBalanceDto balanceDto) {
        return ResponseEntity.ok().body(balanceService.update(balanceDto));
    }
}
