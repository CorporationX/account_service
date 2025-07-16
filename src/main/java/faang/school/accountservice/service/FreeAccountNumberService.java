package faang.school.accountservice.service;

import faang.school.accountservice.config.property.AccountTypeProperties;
import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    @Value("${account.number.init-zeros}")
    private int initZeros;
    @Value("${account.number.generate-per-time}")
    private int generatePerTime;
    @Value("${account.number.base-number-length}")
    private int baseNumberLength;

    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;
    @Autowired
    private AccountTypeProperties typeToNumber;

    @Transactional
    public String getNewNumber(AccountType type) {
        FreeAccountNumber accountNumber = freeAccountNumberRepository
                        .findFirstByType(type.toString())
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
    public void createNewCounter(AccountType type, String baseNumber) {
        if(accountNumberSequenceRepository.existsByType(type)) {
            throw new IllegalArgumentException("Sequence already exists for this type");
        }
        if(baseNumber.length() != baseNumberLength) {
            throw new IllegalArgumentException("Incorrect base number length");
        }
        String newNumber = baseNumber + "0".repeat(initZeros);
        AccountNumberSequence newSeq = new AccountNumberSequence(null, new BigInteger(newNumber), type, 0);
        accountNumberSequenceRepository.save(newSeq);
    }

    private FreeAccountNumber generate(AccountType type) { // When amount of free numbers is too low, auto generating new
        List<FreeAccountNumber> toSave = new ArrayList<>();
        AccountNumberSequence seq = accountNumberSequenceRepository.getByType(type.toString());
        BigInteger number = seq.getNumber();

        for (int i = 0; i < generatePerTime; i++) {
            if(!number.toString().startsWith(typeToNumber.getValue(type))) { // to make first 4 numbers immutable
                number = changeLength(type, seq);
            }
            toSave.add(new FreeAccountNumber(null, String.valueOf(number), type));
            number = number.add(BigInteger.ONE);
        }
        freeAccountNumberRepository.saveAll(toSave);
        accountNumberSequenceRepository.tryIncrement(type.toString(), generatePerTime);

        return freeAccountNumberRepository.findFirstByType(type.toString()).get();
    }

    private BigInteger changeLength(AccountType type, AccountNumberSequence seq) {
        int zeroCount = seq.getNumber().toString().length() - baseNumberLength + 1; // +1 because we need to increase length
        String newNumber = typeToNumber.getValue(type) + "0".repeat(zeroCount);
        seq.setNumber(new BigInteger(newNumber));
        accountNumberSequenceRepository.save(seq);
        return new BigInteger(newNumber);
    }
}
