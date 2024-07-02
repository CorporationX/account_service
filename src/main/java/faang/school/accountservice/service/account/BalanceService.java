package faang.school.accountservice.service.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceService {

    @Transactional
    public void reservePaymentAmount(Long debitAccount, BigDecimal amount) {
        //TODO: написать реализацию метода
    }

    @Transactional
    public void releasePaymentAmount(Long debitAccountId, BigDecimal amount) {
        //TODO: написать реализацию метода
    }

    @Transactional
    public void withdrawFromBalance(Long id, BigDecimal amount) {
        //TODO: написать реализацию метода
    }

    @Transactional
    public void depositToBalance(Long id, BigDecimal amount) {
        //TODO: написать реализацию метода
    }
}
