package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.balance.BalanceDto;
import faang.school.accountservice.model.dto.balance.BalanceOperationRequestDto;
import faang.school.accountservice.model.dto.balance.BalanceTransferRequest;
import faang.school.accountservice.model.dto.balance.TransferResultDto;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{id}")
    public BalanceDto getBalance(@PathVariable("id") long balanceId) {
        return balanceService.getBalance(balanceId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceDto createBalance(@RequestBody @Validated BalanceDto balanceDto) {
        return balanceService.createBalance(balanceDto);
    }

    @PutMapping("/{balanceId}/increasing")
    public BalanceDto increaseBalance(@PathVariable long balanceId, @RequestBody BalanceOperationRequestDto request) {
        return balanceService.increaseBalance(balanceId, request.amount());
    }

    @PutMapping("/{balanceId}/decreasing")
    public BalanceDto decreaseBalance(@PathVariable long balanceId, @RequestBody BalanceOperationRequestDto request) {
        return balanceService.decreaseBalance(balanceId, request.amount());
    }

    @PutMapping("/{balanceId}/reservation")
    public BalanceDto reserveBalance(@PathVariable long balanceId, @RequestBody BalanceOperationRequestDto request) {
        return balanceService.reserveBalance(balanceId, request.amount());
    }

    @PutMapping("/{balanceId}/releasing")
    public BalanceDto releaseBalance(@PathVariable long balanceId, @RequestBody BalanceOperationRequestDto request) {
        return balanceService.releaseReservedBalance(balanceId, request.amount());
    }

    @PutMapping(value = "/transferring")
    public TransferResultDto transferBalance(@RequestBody BalanceTransferRequest request) {
        return balanceService.transferBalance(request.fromAccountId(), request.toAccountId(), request.amount());
    }

    @PatchMapping("/{balanceId}")
    public BalanceDto cancelReservation(@PathVariable long balanceId) {
        return balanceService.cancelReservation(balanceId);
    }
}
