package faang.school.accountservice.service.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceService {

    @Transactional
    public void reservePaymentAmount(Long debitAccount, BigDecimal amount) {
    }
}
