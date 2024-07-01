package faang.school.accountservice.service.savings;

import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.Tariff;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsAccountServiceImpl implements SavingsAccountService {

    private SavingsAccountRepository savingsAccountRepository;
    private TariffRepository tariffRepository;
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public SavingsAccount openSavingsAccount(Long accountId, Long initialTariffId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        Tariff initialTariff = tariffRepository.findById(initialTariffId)
                .orElseThrow(() -> new NotFoundException("Tariff not found"));

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccount(account);
        savingsAccount.setTariffHistory(List.of(initialTariffId));
        savingsAccount.setLastInterestCalculationDate(LocalDate.now());

        return savingsAccountRepository.save(savingsAccount);
    }

    @Override
    public SavingsAccount getSavingsAccount(Long id) {
        return savingsAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Savings account not found"));
    }

    @Override
    public SavingsAccount getSavingsAccountByClientId(Long clientId) {
        return savingsAccountRepository.findByAccount_ClientId(clientId)
                .orElseThrow(() -> new NotFoundException("Savings account not found"));
    }
}
