package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    private static final int INIT_LENGTH = 8;
    private static final int GENERATE_PER_TIME = 5000;
    private static final int BASE_NUMBER_LENGTH = 4;

    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;

    @Transactional
    public String getNewNumber(AccountType type) {
        FreeAccountNumber accountNumber = freeAccountNumberRepository
                        .findFirstByType(type)
                .orElse(generate(type));
        freeAccountNumberRepository.delete(accountNumber);
        return accountNumber.getNumber();
    }

    @Transactional
    public void setConsumer(AccountType type, Consumer<String> consumer) {
        String number = getNewNumber(type);
        consumer.accept(number);
    }

    @Transactional
    public boolean createNewCounter(AccountType type, String initNumber) {
        if(accountNumberSequenceRepository.existsByType(type)) {
            throw new IllegalArgumentException("Sequence already exists for this type");
        }
        accountNumberSequenceRepository.createNewCounter(type, initNumber + "0".repeat(INIT_LENGTH));
        return true;
    }

    @Transactional
    private FreeAccountNumber generate(AccountType type) { // When amount of free numbers is too low, auto generating new
        checkForLength(type);
        List<FreeAccountNumber> toSave = new ArrayList<>();
        BigInteger number = new BigInteger(accountNumberSequenceRepository.getByType(type).getNumber());

        for (int i = 0; i < GENERATE_PER_TIME; i++) {
            toSave.add(new FreeAccountNumber(null, String.valueOf(number), type));
            number = number.add(BigInteger.ONE);
        }
        freeAccountNumberRepository.saveAll(toSave);
        accountNumberSequenceRepository.tryIncrement(type, 5000);

        return freeAccountNumberRepository.findFirstByType(type).get();
    }

    @Transactional
    private void checkForLength(AccountType type) { // to make first 4 numbers immutable
        AccountNumberSequence seq = accountNumberSequenceRepository.getByType(type);
        BigInteger number = new BigInteger(seq.getNumber());
        number = number.add(BigInteger.valueOf(GENERATE_PER_TIME));
        String incremented = number.toString();

        if (!incremented.startsWith(type.getNumber())) {
            int zeroCount = incremented.length() - BASE_NUMBER_LENGTH + 1; // +1 because we need to increase length
            String newNumber = type.getNumber() + "0".repeat(zeroCount);
            seq.setNumber(newNumber);
            accountNumberSequenceRepository.save(seq);
        }
    }
}
