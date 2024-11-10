package faang.school.accountservice.service.balance.changebalance;

import faang.school.accountservice.enums.ChangeBalanceType;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.service.balance.operation.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public interface BalanceChanger {

    Balance processBalance(Balance balance, BigDecimal amount, Operation operation);

    ChangeBalanceType getChangeBalanceType();

    @Autowired
    default void setChangeBalanceType(BalanceChangeRegistry changeBalanceStorage) {
        changeBalanceStorage.registerBalanceChange(this);
    }
}