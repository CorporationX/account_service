package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.PaymentStatus;
import faang.school.accountservice.event.AuthorizationMessageEvent;
import faang.school.accountservice.event.AuthorizationMessageResultEvent;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.publisher.AuthorizationMessageResultEventPublisher;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final AccountService accountService;
    private final AuthorizationMessageResultEventPublisher authorizationMessageResultEventPublisher;

    @Transactional
    public BalanceDto createBalance(Account account, BigDecimal authorisationBalance, BigDecimal actualBalance) {
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setActualBalance(actualBalance);
        balance.setAuthorisationBalance(authorisationBalance);
        balance.setCreatedAt(LocalDateTime.now());
        balance.setUpdatedAt(LocalDateTime.now());
        balance.setVersion(1L);

        Balance savedBalance = balanceRepository.save(balance);

        return balanceMapper.toDto(savedBalance);
    }

    @Transactional
    public BalanceDto updateBalance(Long balanceId, BigDecimal authorisationBalance, BigDecimal actualBalance) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found with id" + balanceId));

        balance.setAuthorisationBalance(authorisationBalance);
        balance.setActualBalance(actualBalance);
        balance.setUpdatedAt(LocalDateTime.now());
        balance.setVersion(balance.getVersion() + 1);

        Balance savedBalance = balanceRepository.save(balance);

        return balanceMapper.toDto(savedBalance);
    }

    @Transactional
    public BalanceDto getBalance(Long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found with id: " + balanceId));
        return balanceMapper.toDto(balance);
    }

    public void reserveMoneyOnAuthorisationBalance(AuthorizationMessageEvent authorization){
        Account account = accountService.getAccountByNumber(authorization.getAccountNumber());

        Balance balance = balanceRepository.findByAccountId(account.getId());

        AuthorizationMessageResultEvent resultEvent = new AuthorizationMessageResultEvent(
                authorization.getAccountNumber(),
                authorization.getIdempotencyToken());

        if(balance.getAuthorisationBalance().doubleValue()<authorization.getAmount().doubleValue()){
            resultEvent.setPaymentStatus(PaymentStatus.CLOSED);
            authorizationMessageResultEventPublisher.publish(resultEvent);
        }

        else {
            BigDecimal updatedBalance = balance.getAuthorisationBalance().subtract(authorization.getAmount());
            balance.setAuthorisationBalance(updatedBalance);
            balanceRepository.save(balance);

            resultEvent.setPaymentStatus(PaymentStatus.RESERVED);
            authorizationMessageResultEventPublisher.publish(resultEvent);
        }
    }
}

