package faang.school.accountservice.service;

import faang.school.accountservice.model.account.freeaccounts.AccountType;
import faang.school.accountservice.model.account.freeaccounts.FreeAccountId;
import faang.school.accountservice.model.account.freeaccounts.FreeAccountNumber;
import faang.school.accountservice.model.account.sequence.AccountSeq;
import faang.school.accountservice.repository.account.freeaccounts.FreeAccountRepository;
import faang.school.accountservice.repository.account.sequence.AccountSeqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    private static final long DEBIT_ACCOUNT = 4200_0000_0000_0000L;
    private static final long CREDIT_ACCOUNT = 5536_0000_0000_0000L;

    private final AccountSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;

    @Transactional
    public void generateOneAccountNumber(AccountType type) {
        AccountSeq period = accountSeqRepository.incrementCounter(type.name());
        FreeAccountNumber accountNumber = new FreeAccountNumber();
        long baseAccountNumber;

        if ("credit".equalsIgnoreCase(type.name())) {
            baseAccountNumber = CREDIT_ACCOUNT;
        } else if ("debit".equalsIgnoreCase(type.name())) {
            baseAccountNumber = DEBIT_ACCOUNT;
        } else {
            throw new IllegalArgumentException("Unsupported account type: " + type.name());
        }

        accountNumber.setId(new FreeAccountId(type, baseAccountNumber + period.getCounter()));
        freeAccountRepository.save(accountNumber);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountRepository.retrieveFirst(type.name()));
    }
}
