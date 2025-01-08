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

import static faang.school.accountservice.entity.AccountType.DEBIT;
import static faang.school.accountservice.entity.AccountType.SAVINGS;

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
            throw new RuntimeException("The number of available rooms is more than 10");
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
            if (type.equals(DEBIT)) {
                numbers.add(new FreeAccountNumber(new FreeAccountId(type, DEBIT.getAccountNumber() + i)));
            } else if (type.equals(SAVINGS)) {
                numbers.add(new FreeAccountNumber(new FreeAccountId(type, SAVINGS.getAccountNumber() + i)));
            }
            freeAccountRepository.saveAll(numbers);
            log.info("In the table free_account_numbers has save free number.");
        }
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, FreeAccountNumber freeAccountNumber, Consumer<FreeAccountNumber> numberConsumer) {
        log.info("In the table free_account_numbers delete number.");
        numberConsumer.accept(freeAccountNumber);
    }

    @Transactional
    public void createFreeNumber(RequestAccount requestAccount) {
        validatePreviouslyReceivedNumber(requestAccount);
        FreeAccountNumber freeAccountNumber = freeAccountRepository.retrieveFirst(requestAccount.getType().name());
        if (freeAccountNumber == null) {
            log.info("No free account number found.");
            generateAccountNumbers(requestAccount.getType(), 5);
            createAccountWithFreeNumber(requestAccount, freeAccountRepository.retrieveFirst(requestAccount.getType().name()));
        } else {
            createAccountWithFreeNumber(requestAccount, freeAccountNumber);
        }
    }

    @Transactional
    public void createAccountWithFreeNumber(RequestAccount requestAccount, FreeAccountNumber freeAccountNumber) {
        retrieveAccountNumber(requestAccount.getType(), freeAccountNumber, numberConsumer -> {
            Account newAccount = new Account();
            newAccount.setOwnerAccount(requestAccount.getOwner_account());
            newAccount.setUserOwnerId(requestAccount.getUser_owner_id());
            newAccount.setNumber(freeAccountNumber.getId().getAccountNumber());
            newAccount.setType(requestAccount.getType());
            newAccount.setCurrency("currency");
            newAccount.setVersion(1L);
            accountRepository.saveAndFlush(newAccount);
            log.info("Save number in basa accoun");
        });
    }

    public void validatePreviouslyReceivedNumber(RequestAccount reqwestAccount) {
        Account account = accountRepository.findById(reqwestAccount.getUser_owner_id()).orElse(null);
        if (account != null) {
            if (reqwestAccount.getUser_owner_id() == account.getUserOwnerId() &&
                    reqwestAccount.getType() == account.getType()) {
                throw new RuntimeException("A number has already been created for this user "
                        + account.getUserOwnerId()
                        + " in type " + account.getType());
            } else {
                log.info("Account not found for user ID: {}", reqwestAccount.getUser_owner_id());
            }
        }
    }
}