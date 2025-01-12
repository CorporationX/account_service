package faang.school.accountservice.service;

import faang.school.accountservice.dto.Accoun.RequestAccount;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {

    private final AccountSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        int allNumberNow = freeAccountRepository.allNumberTypeNow(type.name());
        if (allNumberNow >= 10) {
            log.info("The number of available rooms is more than 10.");
            return;
        }
        List<Object[]> result = accountSeqRepository.incrementCounter(type.name(), batchSize);
        Object[] row = result.get(0);
        String typeStr = (String) row[0];
        long counter = (Long) row[1];
        long initialCounter = (Long) row[2];
        AccountType accountType = AccountType.valueOf(typeStr);
        log.info("Executing update: type={}, counter={}, initialValue={}", accountType.name(), counter, initialCounter);
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = initialCounter; i < counter; i++) {
            numbers.add(new FreeAccountNumber(new FreeAccountId(type, type.getAccountNumber() + i)));
            freeAccountRepository.saveAll(numbers);
            log.info("In the table free_account_numbers has save free number.");
        }
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        FreeAccountNumber freeAccountNumber = freeAccountRepository.retrieveFirst(type.name());
        log.info("In the table free_account_numbers delete number.");
        numberConsumer.accept(freeAccountNumber);
    }

    @Transactional
    public void createFreeNumber(RequestAccount requestAccount) {
        validatePreviouslyReceivedNumber(requestAccount);
        int freeAccountNumber = freeAccountRepository.allNumberTypeNow(requestAccount.getType().name());
        if (freeAccountNumber == 0) {
            log.info("No free account number found.");
            generateAccountNumbers(requestAccount.getType(), 5);
            createAccountWithFreeNumber(requestAccount);
        } else {
            createAccountWithFreeNumber(requestAccount);
        }
    }

    @Transactional
    public void createAccountWithFreeNumber(RequestAccount requestAccount) {
        retrieveAccountNumber(requestAccount.getType(), numberConsumer -> {
            Account newAccount = new Account();
            newAccount.setOwnerAccount(requestAccount.getOwnerAccount());
            newAccount.setUserOwnerId(requestAccount.getUserOwnerId());
            newAccount.setNumber(numberConsumer.getId().getAccountNumber());
            newAccount.setType(requestAccount.getType());
            newAccount.setCurrency(requestAccount.getCurrency().name());
            newAccount.setVersion(requestAccount.getVersion());
            newAccount.setProjectOwnerId(requestAccount.getProjectOwnerId());
            accountRepository.saveAndFlush(newAccount);
            log.info("Save number in basa accoun");
        });
    }

    private void validatePreviouslyReceivedNumber(RequestAccount reqwestAccount) {
        if (reqwestAccount.getUserOwnerId() != null) {
            Account account = accountRepository.findById(reqwestAccount.getUserOwnerId()).orElse(null);
            if (account != null) {
                if (reqwestAccount.getUserOwnerId() == account.getUserOwnerId() &&
                        reqwestAccount.getType() == account.getType()) {
                    throw new RuntimeException("A number has already been created for this user "
                            + account.getUserOwnerId()
                            + " in type " + account.getType());
                } else {
                    log.info("Account not found for user ID: {}", reqwestAccount.getUserOwnerId());
                }
            }
        } else {
            Account accountProject = accountRepository.findById(reqwestAccount.getProjectOwnerId()).orElse(null);
            if (accountProject != null) {
                if (reqwestAccount.getProjectOwnerId() == accountProject.getProjectOwnerId() &&
                        reqwestAccount.getType() == accountProject.getType()) {
                    throw new RuntimeException("A number has already been created for this user "
                            + accountProject.getUserOwnerId()
                            + " in type " + accountProject.getType());
                } else {
                    log.info("Accountproject not found for user ID: {}", reqwestAccount.getUserOwnerId());

                }
            }
        }
    }
}