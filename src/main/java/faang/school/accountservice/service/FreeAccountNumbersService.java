package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService{

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountRepository;

    private final static long ACCOUNT_PATTERN_DEBIT=4200_0000_0000_0000L;
    private final static long ACCOUNT_PATTERN_DEPO=5236_0000_0000_0000L;
    private final static int MAX_ACCOUNT_LEADING_DIGITS_DEBIT=4200;
    private final static int MAX_ACCOUNT_LEADING_DIGITS_DEPO=5236;

    @Transactional
    public void generateAccountNumber(AccountType accountType, long bucketSize){
        // Increment the counter for the specified account type
        accountNumbersSequenceRepository.incrementCounter(accountType.name(), bucketSize);

        // Retrieve the updated counter value after incrementing
        AccountSeq accountSeq = accountNumbersSequenceRepository.findByType(accountType.name());
        long initialCounter = accountSeq.getCounter() - bucketSize; // Starting point for account number generation

        List<FreeAccountNumber> accountNumberList = new ArrayList<>();
        for (long i = initialCounter; i < accountSeq.getCounter(); i++) {
            long accountNumber;
            if (accountType.equals(AccountType.DEBIT)) {
                accountNumber = ACCOUNT_PATTERN_DEBIT + i;
                if (accountNumber / 1_0000_0000_0000L != MAX_ACCOUNT_LEADING_DIGITS_DEBIT) {
                    throw new IllegalStateException("Generated account number exceeds pattern limit.");
                }
            } else {
                accountNumber = ACCOUNT_PATTERN_DEPO + i;
                if (accountNumber / 1_0000_0000_0000L != MAX_ACCOUNT_LEADING_DIGITS_DEPO) {
                    throw new IllegalStateException("Generated account number exceeds pattern limit.");
                }
            }
            accountNumberList.add(new FreeAccountNumber(new FreeAccountId(accountType, accountNumber)));
        }

        // Save the generated account numbers to the repository
        freeAccountRepository.saveAll(accountNumberList);
    }


    @Transactional
    public void retrieveAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> accountNumberConsumer){
        Optional<FreeAccountNumber> accountNumber = freeAccountRepository.retrieveFirst(accountType.name());
        if(accountNumber.isPresent()){
            accountNumberConsumer.accept(accountNumber.get());
        }else{
            generateAccountNumber(accountType, 1);
            retrieveAccountNumber(accountType, accountNumberConsumer);

        }

    }
}
