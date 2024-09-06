package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.repository.account.SavingsAccountRepository;
import faang.school.accountservice.validator.InterestCalculationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestCalculationService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountService savingsAccountService;
    private final InterestCalculationValidator interestCalculationValidator;

    @Transactional
    public void calculateInterest() {
        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findAll();
        for (SavingsAccount account : savingsAccounts) {
            if (interestCalculationValidator.validateLastInterestCalculationDate(account.getLastInterestCalculationDate())) {

                BigDecimal interestRate = BigDecimal.valueOf(Integer.parseInt(savingsAccountService.getSavingAccountById(account.getId()).getRate()));
                BigDecimal balance = account.getAccount().getBalance();
                BigDecimal interest = balance.multiply(interestRate).divide(BigDecimal.valueOf(100));

                balance = balance.add(interest);
                account.getAccount().setBalance(balance);

                account.setLastInterestCalculationDate(LocalDateTime.now());
                savingsAccountRepository.save(account);
            }
        }
    }
}
