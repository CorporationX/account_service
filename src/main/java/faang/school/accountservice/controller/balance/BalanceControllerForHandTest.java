package faang.school.accountservice.controller.balance;

import com.github.f4b6a3.uuid.UuidCreator;
import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.balance.response.AuthPaymentResponseDto;
import faang.school.accountservice.dto.balance.response.BalanceResponseDto;
import faang.school.accountservice.mapper.balance.AuthPaymentMapper;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.balance.AuthPayment;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/balances/test")
@RestController
public class BalanceControllerForHandTest {
    private final BalanceService balanceService;
    private final AuthPaymentMapper authPaymentMapper;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @PostMapping("/create")
    public UUID createAccountTEST() {
        Account account = new Account(UuidCreator.getTimeBased(), "232423242324", null);
        account = accountRepository.save(account);
        return account.getId();
    }

    @PostMapping("/create/{accountId}")
    public BalanceResponseDto createBalanceTEST(@PathVariable UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        Balance balance = balanceService.createBalance(account);
        return balanceMapper.toBalanceResponseDto(balance);
    }

    @PostMapping("/payment/top-up-balance/{balanceId}")
    public BalanceResponseDto topUpCurrentBalanceTEST(@PathVariable UUID balanceId, @RequestBody Money money) {
        Balance balance = balanceService.topUpCurrentBalance(balanceId, money);
        return balanceMapper.toBalanceResponseDto(balance);
    }

    @PostMapping("/payment/authorize/{balanceId}")
    public AuthPaymentResponseDto authorizePaymentTEST(@PathVariable UUID balanceId, @RequestBody Money money) {
        AuthPayment payment = balanceService.authorizePayment(balanceId, money);
        return authPaymentMapper.toAuthPaymentResponseDto(payment);
    }

    @PostMapping("/payment/accept/{authPaymentId}")
    public AuthPaymentResponseDto acceptPaymentTEST(@PathVariable UUID authPaymentId, @RequestBody Money money) {
        AuthPayment payment = balanceService.acceptPayment(authPaymentId, money);
        return authPaymentMapper.toAuthPaymentResponseDto(payment);
    }

    @PostMapping("/payment/reject/{authPaymentId}")
    public AuthPaymentResponseDto rejectPaymentTEST(@PathVariable UUID authPaymentId) {
        AuthPayment payment = balanceService.rejectPayment(authPaymentId);
        return authPaymentMapper.toAuthPaymentResponseDto(payment);
    }

    @PostMapping("/payment/top-up-balance/{balanceId}/{value}")
    public BalanceResponseDto multiplyCurrentBalanceTEST(@PathVariable UUID balanceId, @PathVariable double value) {
        Balance balance = balanceService.multiplyCurrentBalance(balanceId, value);
        return balanceMapper.toBalanceResponseDto(balance);
    }
}
