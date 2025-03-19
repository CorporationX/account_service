package faang.school.accountservice.controller;

import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountOptimisticLockTest extends BaseIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testOptimisticLocking() {
        Account account = Account.builder()
                .accountNumber("12345678901234")
                .ownerId(100L)
                .ownerType(OwnerType.USER)
                .accountType(AccountType.PERSONAL)
                .currency(Currency.USD)
                .build();

        Account savedAccount = accountRepository.save(account);
        assertEquals(0, savedAccount.getVersion());

        Account concurrentUpdate = accountRepository.findById(savedAccount.getId()).orElseThrow();

        savedAccount.setAccountType(AccountType.BUSINESS);
        accountRepository.save(savedAccount);

        concurrentUpdate.setAccountType(AccountType.FOREIGN_CURRENCY);

        Exception exception = null;
        try {
            accountRepository.save(concurrentUpdate);
        } catch (Exception e) {
            exception = e;
        }

        assertEquals(ObjectOptimisticLockingFailureException.class, exception.getClass());
    }
}