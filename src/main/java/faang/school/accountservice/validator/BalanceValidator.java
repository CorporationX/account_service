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
        checkBalanceIdNullable(balance.getId());
        checkActualAndAuthorizeBalances(balance);
    }

    public void checkAccountId(Long id) {
        checkIfBalanceNullable(id);
        checkIfBalanceByAccountAlreadyExists(id);
    }

    public void checkIfBalanceNullable(Long accountId) {
        if (accountId == null) {
            throw new DataValidationException("Account id can't be nullable");
        }
    }

    public void checkBalanceIdNullable(UUID id) {
        if (id == null) {
            throw new DataValidationException("Balance id is nullable");
        }
    }

    public void checkIfBalanceByAccountAlreadyExists(Long accountId) {
        if (balanceRepository.getByAccountId(accountId).isPresent()) {
            throw new DataValidationException(String.format("Balance for account [%s] already exists", accountId));
        }
    }

    public void checkActualAndAuthorizeBalances(BalanceDto balance) {
        if (balance.getAuthorizedBalance().compareTo(balance.getActualBalance()) > 0) {
            throw new DataValidationException("Authorize balance can't be bigger than actual balance");
        }
    }

//    public void checkBalanceForAuthorization(BalanceDto balanceDto, BigDecimal amount) {
//        if (balanceDto.getAuthorizedBalance().add(amount).compareTo(balanceDto.getActualBalance()) > 0) {
//            throw new DataValidationException("Not enough money for authorize");
//        }
//    }
//
//    public void checkBalanceForWriteOff(BalanceDto balanceDto, BigDecimal amount) {
//        if (balanceDto.getAuthorizedBalance()) {
//
//        }
//    }
}
