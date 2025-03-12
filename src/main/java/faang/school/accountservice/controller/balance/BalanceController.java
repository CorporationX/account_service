package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.balance.AuthPaymentResponseDto;
import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.mapper.AuthPaymentMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.service.balance.BalanceService;
import feign.Body;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RequiredArgsConstructor
@RequestMapping("/api/v1/balances")
@RestController
public class BalanceController {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;
    private  final AuthPaymentMapper authPaymentMapper;

    @GetMapping("/{balanceId}")
    public BalanceResponseDto findBalanceByAccountNumber(@PathVariable UUID balanceId) {
        Balance balance = balanceService.findById(balanceId);

        return balanceMapper.toBalanceResponseDto(balance);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{authPaymentId}")
    public AuthPaymentResponseDto acceptPayment(@PathVariable UUID authPaymentId, @RequestBody Money money) {
        AuthPayment payment = balanceService.acceptPayment(authPaymentId,money);

        return authPaymentMapper.toDto(payment);
    }

}
