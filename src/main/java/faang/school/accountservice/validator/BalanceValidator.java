package faang.school.accountservice.validator;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class BalanceValidator {

    private final BalanceRepository balanceRepository;

    public void checkBalanceToCreate(BalanceDto balance) {
        checkAccountId(balance.getAccountId());
    }

    public void checkBalanceToUpdate(BalanceDto balance) {
        checkActualAndAuthorizeBalances(balance);
    }

    public void checkAccountId(Long id) {
        checkIfAccountIdNullable(id);
    }

    public void checkIfAccountIdNullable(Long accountId) {
        if (accountId == null) {
            throw new DataValidationException("Account id can't be nullable");
        }
    }

    public void checkActualAndAuthorizeBalances(BalanceDto balance) {
        if (balance.getAuthorizedBalance().compareTo(balance.getActualBalance()) > 0) {
            throw new DataValidationException("Authorize balance can't be bigger than actual balance");
        }
    }
}
