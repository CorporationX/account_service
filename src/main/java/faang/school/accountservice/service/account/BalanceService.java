package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class BalanceService {

    @Transactional
    public boolean reservePaymentAmount(Account account, BigDecimal amount) {
        //TODO: написать реализацию метода
        return true;
    }

    @Transactional
    public void releasePaymentAmount(Long accountId, BigDecimal amount) {
        log.info("Founds in amount of {} were released from account balance with id {}", amount, accountId);
        //TODO: написать реализацию метода
    }

    @Transactional
    public void withdrawFromBalance(Long accountId, BigDecimal amount) {
        log.info("Founds in amount of {} were withdraw from account balance with id {}", amount, accountId);
        //TODO: написать реализацию метода
    }

    @Transactional
    public void depositToBalance(Long accountId, BigDecimal amount) {
        log.info("Founds in amount of {} were credited to account balance with id {}", amount, accountId);
        //TODO: написать реализацию метода
    }
}
