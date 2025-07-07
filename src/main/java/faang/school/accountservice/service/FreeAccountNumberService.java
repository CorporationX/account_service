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
    private static final int INIT_ZEROS_LENGTH = 8;
    private static final int GENERATE_PER_TIME = 5000;
    private static final int BASE_NUMBER_LENGTH = 4;

    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;

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
    public boolean createNewCounter(AccountType type, String initNumber) {
        if(accountNumberSequenceRepository.existsByType(type.toString())) {
            throw new IllegalArgumentException("Sequence already exists for this type");
        }
        String newNumber = initNumber + "0".repeat(INIT_ZEROS_LENGTH);
        AccountNumberSequence newSeq = new AccountNumberSequence(null, new BigInteger(newNumber), type, 0);
        accountNumberSequenceRepository.save(newSeq);
        return true;
    }

    @Transactional
    private FreeAccountNumber generate(AccountType type) { // When amount of free numbers is too low, auto generating new
        List<FreeAccountNumber> toSave = new ArrayList<>();
        AccountNumberSequence seq = accountNumberSequenceRepository.getByType(type.toString());
        BigInteger number = seq.getNumber();

        for (int i = 0; i < GENERATE_PER_TIME; i++) {
            if(!number.toString().startsWith(type.getNumber())) { // to make first 4 numbers immutable
                number = changeLength(type, seq);
            }
            toSave.add(new FreeAccountNumber(null, String.valueOf(number), type));
            number = number.add(BigInteger.ONE);
        }
        freeAccountNumberRepository.saveAll(toSave);
        accountNumberSequenceRepository.tryIncrement(type.toString(), GENERATE_PER_TIME);

        return freeAccountNumberRepository.findFirstByType(type.toString()).get();
    }

    @Transactional
    private BigInteger changeLength(AccountType type, AccountNumberSequence seq) {
        int zeroCount = seq.getNumber().toString().length() - BASE_NUMBER_LENGTH + 1; // +1 because we need to increase length
        String newNumber = type.getNumber() + "0".repeat(zeroCount);
        seq.setNumber(new BigInteger(newNumber));
        accountNumberSequenceRepository.save(seq);
        return new BigInteger(newNumber);
    }

//    @Scheduled(fixedDelay = 3600000, initialDelay = 0)
//    public void generateIfFewLeft() {
//        for (AccountType type : AccountType.values()) {
//            if (freeAccountNumberRepository.findAmountByType(type) < 10000) {
//                generate(type);
//            }
//        }
//    }
}
