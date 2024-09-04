package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.AccountNumberPool;
import faang.school.accountservice.repository.account.AccountNumberPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountNumberService {

    private final AccountNumberPoolRepository accountNumberPoolRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Transactional
    public synchronized String getAccountNumber() {
        AccountNumberPool accountNumberPool = accountNumberPoolRepository.findTopByOrderByIdAsc();

        if (accountNumberPool == null) {
            populateAccountNumbers(10);
            accountNumberPool = accountNumberPoolRepository.findTopByOrderByIdAsc();
        }

        String accountNumber = accountNumberPool.getAccountNumber();
        accountNumberPoolRepository.deleteByAccountNumber(accountNumber);
        return accountNumber;
    }

    @Transactional
    public void populateAccountNumbers(int count) {
        for (int i = 0; i < count; i++) {
            String accountNumber = accountNumberGenerator.generateAccountNumber();
            AccountNumberPool accountNumberPool = new AccountNumberPool();
            accountNumberPool.setAccountNumber(accountNumber);
            accountNumberPool.setId(UUID.randomUUID());
            accountNumberPoolRepository.save(accountNumberPool);
        }
    }

    @Transactional(readOnly = true)
    public int getAccountNumberPoolSize() {
        return (int) accountNumberPoolRepository.count();
    }
}
